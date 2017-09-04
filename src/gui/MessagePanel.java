package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import model.Message;
import controller.MessageServer;

public class MessagePanel extends JPanel implements ProgressDialogListener {

	private JTree serverTree;
	private ServerTreeCellRenderer treeCellRenderer;
	private ServerTreeCellEditor treeCellEditor;
	private ProgressDialog progressDialog;
	private SwingWorker<List<Message>, Integer> worker;
	private TextPanel textPanel;
	private JList messageList;
	private JSplitPane upperPane;
	private JSplitPane lowerPane;
	private DefaultListModel messageListModel;
	
	private String language;
	private String country;
	private Locale currentLocale;
	private ResourceBundle messages;
	private App app;

	private Set<Integer> selectedServers;
	private MessageServer messageServer;

	public MessagePanel(JFrame parent, Locale currentLocale, ResourceBundle messages, String language, String country) {
		messageListModel = new DefaultListModel();
		progressDialog = new ProgressDialog(parent, messages.getString("Messages_Downloading"), currentLocale, messages, language, country);
		this.language = language;
		this.country = country;
		this.currentLocale = currentLocale;
		this.messages = messages;
		
		messageServer = new MessageServer(currentLocale, messages, language, country);
		
		progressDialog.setListener(this);
		
		selectedServers = new TreeSet<Integer>();
		selectedServers.add(0);
		selectedServers.add(1);
		selectedServers.add(4);

		treeCellRenderer = new ServerTreeCellRenderer();
		treeCellEditor = new ServerTreeCellEditor();

		serverTree = new JTree(createTree());
		serverTree.setCellRenderer(treeCellRenderer);
		serverTree.setCellEditor(treeCellEditor);
		serverTree.setEditable(true);

		serverTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		messageServer.setSelectedServers(selectedServers);

		treeCellEditor.addCellEditorListener(new CellEditorListener() {
			public void editingCanceled(ChangeEvent e) {
			}

			public void editingStopped(ChangeEvent e) {
				ServerInfo info = (ServerInfo) treeCellEditor.getCellEditorValue();

				int serverId = info.getId();

				if (info.isChecked()) {
					selectedServers.add(serverId);
				} else {
					selectedServers.remove(serverId);
				}

				messageServer.setSelectedServers(selectedServers);

				retrieveMessages();
			}
		});

		setLayout(new BorderLayout());
		
		textPanel = new TextPanel();
		messageList = new JList(messageListModel);
		messageList.setCellRenderer(new MessageListRenderer());
		
		lowerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(messageList), textPanel);
		upperPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(serverTree), lowerPane);
		textPanel.setMinimumSize(new Dimension(0, 100));
		messageList.setMinimumSize(new Dimension(0, 100));
		
		upperPane.setResizeWeight(0.5);
		lowerPane.setResizeWeight(0.5);
		
		add(upperPane, BorderLayout.CENTER);
	}

	public void refresh(){
		retrieveMessages();
	}
	
	private void retrieveMessages() {

		progressDialog.setMaximum(messageServer.getMessageCount());
		progressDialog.setVisible(true);
			
		worker = new SwingWorker<List<Message>, Integer>() {

			@Override
			protected void done() {
				progressDialog.setVisible(false);
				if(isCancelled()) return;
				try {
					List<Message> retrievedMessages = get();
					messageListModel.removeAllElements();
					for(Message message: retrievedMessages){
						messageListModel.addElement(message);
					}
					System.out.println("Retrieved " + retrievedMessages.size() + " messages.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			protected void process(List<Integer> counts) {
				int retrieved = counts.get(counts.size() - 1);
				progressDialog.setValue(retrieved);
			}

			@Override
			protected List<Message> doInBackground() throws Exception {
				List<Message> retrievedMessages = new ArrayList<Message>();
				int count = 0;		
				for (Message message : messageServer) {
					if(isCancelled()) break;
					System.out.println(message.getTitle());
					retrievedMessages.add(message);
					count++;
					publish(count);
				}
				return retrievedMessages;
			}
		};
		
		worker.execute();
		
	}

	private DefaultMutableTreeNode createTree() {

		TimeZone timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
		TimeZone timeZone2 = TimeZone.getTimeZone("America/New_York");
		TimeZone timeZone3 = TimeZone.getTimeZone("Europe/Paris");
		Calendar calendar = new GregorianCalendar();

		int AM_PM = 0;
		String AM = "AM";
		String PM = "PM";
		String AM_PMValue;

		if (app.frenchMenuState == true) {
			Locale localeFR = new Locale("fr", "FR");
			DateFormat dateFormatFR = DateFormat.getDateInstance(DateFormat.DEFAULT, localeFR);
			String dateFR = dateFormatFR.format(new Date());

			DefaultMutableTreeNode top = new DefaultMutableTreeNode(messages.getString("Servers"));

			DefaultMutableTreeNode branch1 = new DefaultMutableTreeNode("USA");
			calendar.setTimeZone(timeZone2);
			long timeNY = calendar.getTimeInMillis();
			if (calendar.get(Calendar.AM_PM) == 1) {
				AM_PMValue = PM;
			} else {
				AM_PMValue = AM;
			}
			DefaultMutableTreeNode server1 = new DefaultMutableTreeNode(
					new ServerInfo("New York", 0, dateFR,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(0)));
			DefaultMutableTreeNode server2 = new DefaultMutableTreeNode(
					new ServerInfo("Boston", 1, dateFR,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(1)));
			calendar.setTimeZone(timeZone1);
			long timeLA = calendar.getTimeInMillis();
			if (calendar.get(Calendar.AM_PM) == 1) {
				AM_PMValue = PM;
			} else {
				AM_PMValue = AM;
			}
			DefaultMutableTreeNode server3 = new DefaultMutableTreeNode(
					new ServerInfo("Los Angeles", 2, dateFR,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(2)));

			branch1.add(server1);
			branch1.add(server2);
			branch1.add(server3);

			DefaultMutableTreeNode branch2 = new DefaultMutableTreeNode("France");
			calendar.setTimeZone(timeZone3);
			long timeParis = calendar.getTimeInMillis();
			if (calendar.get(Calendar.AM_PM) == 1) {
				AM_PMValue = PM;
			} else {
				AM_PMValue = AM;
			}
			DefaultMutableTreeNode server4 = new DefaultMutableTreeNode(
					new ServerInfo("Paris", 3, dateFR,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(3)));
			DefaultMutableTreeNode server5 = new DefaultMutableTreeNode(
					new ServerInfo("Lyon", 4, dateFR,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(4)));

			branch2.add(server4);
			branch2.add(server5);

			top.add(branch1);
			top.add(branch2);

			return top;
		}

		else {
			Locale localeUS = new Locale("en", "US");
			DateFormat dateFormatUS = DateFormat.getDateInstance(DateFormat.DEFAULT, localeUS);
			String dateUS = dateFormatUS.format(new Date());

			DefaultMutableTreeNode top = new DefaultMutableTreeNode("Servers");

			DefaultMutableTreeNode branch1 = new DefaultMutableTreeNode("USA");
			calendar.setTimeZone(timeZone2);
			long timeNY = calendar.getTimeInMillis();
			if (calendar.get(Calendar.AM_PM) == 1) {
				AM_PMValue = PM;
			} else {
				AM_PMValue = AM;
			}
			DefaultMutableTreeNode server1 = new DefaultMutableTreeNode(
					new ServerInfo("New York", 0, dateUS,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(0)));
			DefaultMutableTreeNode server2 = new DefaultMutableTreeNode(
					new ServerInfo("Boston", 1, dateUS,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(1)));
			calendar.setTimeZone(timeZone1);
			long timeLA = calendar.getTimeInMillis();
			if (calendar.get(Calendar.AM_PM) == 1) {
				AM_PMValue = PM;
			} else {
				AM_PMValue = AM;
			}
			DefaultMutableTreeNode server3 = new DefaultMutableTreeNode(
					new ServerInfo("Los Angeles", 2, dateUS,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(2)));

			branch1.add(server1);
			branch1.add(server2);
			branch1.add(server3);

			DefaultMutableTreeNode branch2 = new DefaultMutableTreeNode(
					"France");
			calendar.setTimeZone(timeZone3);
			long timeParis = calendar.getTimeInMillis();
			if (calendar.get(Calendar.AM_PM) == 1) {
				AM_PMValue = PM;
			} else {
				AM_PMValue = AM;

			}
			DefaultMutableTreeNode server4 = new DefaultMutableTreeNode(
					new ServerInfo("Paris", 3, dateUS,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(3)));
			DefaultMutableTreeNode server5 = new DefaultMutableTreeNode(
					new ServerInfo("Lyon", 4, dateUS,
							calendar.get(Calendar.HOUR_OF_DAY),
							calendar.get(Calendar.MINUTE),
							calendar.get(Calendar.SECOND), AM_PMValue,
							selectedServers.contains(4)));

			branch2.add(server4);
			branch2.add(server5);

			top.add(branch1);
			top.add(branch2);

			
			return top;
		}
	
}
	

	@Override
	public void progressDialogCancelled() {
		if(worker != null) {
			worker.cancel(true);
		}
	}

}
