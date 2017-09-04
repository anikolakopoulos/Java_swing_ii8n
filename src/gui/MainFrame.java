package gui;

import controller.Controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainFrame extends JFrame {

	private Toolbar toolbar;
	private FormPanel formPanel;
	private JFileChooser fileChooser;
	private Controller controller;
	private TablePanel tablePanel;
	private PrefsDialog prefsDialog;
	private Preferences prefs;
	private JSplitPane splitPane;
	private JTabbedPane tabPane;
	private MessagePanel messagePanel;
	private String language;
	private String country;
	private Locale currentLocale;
	private ResourceBundle messages;
	private String languageFr;
	private String countryFr;
	private String languageEn;
	private String countryEn;
	private ImageIcon US_Flag;
	private ImageIcon French_Flag;
	private String z_choosertitleImport;
	private String z_choosertitleExport;

	public MainFrame(Locale currentLocale, ResourceBundle messages, String language, String country) {
		super(messages.getString("People_Form_Database"));
		this.language = language;
		this.country = country;
		this.currentLocale = currentLocale;
		this.messages = messages;

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Can't set look and feel.");
		}

		setLayout(new BorderLayout());

		toolbar = new Toolbar(currentLocale, messages, language, country);
		formPanel = new FormPanel(currentLocale, messages, language, country);
		tablePanel = new TablePanel(currentLocale, messages, language, country);
		prefsDialog = new PrefsDialog(this, currentLocale, messages, language, country);
		tabPane = new JTabbedPane();
		messagePanel = new MessagePanel(this, currentLocale, messages, language, country);
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tabPane);
		splitPane.setOneTouchExpandable(true);

		tabPane.addTab(messages.getString("Person_Database"), tablePanel);
		tabPane.addTab("Messages", messagePanel);

		US_Flag = new ImageIcon("i18n/flags/US_Flag.png");
		French_Flag = new ImageIcon("i18n/flags/French_Flag.png");

		prefs = Preferences.userRoot().node("db");

		controller = new Controller();

		tablePanel.setData(controller.getPeople());

		tablePanel.setPersonTableListener(new PersonTableListener() {
			public void rowDeleted(int row) {
				controller.removePerson(row);
			}
		});

		tabPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int tabIndex = tabPane.getSelectedIndex();

				if (tabIndex == 1) {
					messagePanel.refresh();
				}
			}
		});

		prefsDialog.setPrefsListener(new PrefsListener() {
			public void preferencesSet(String user, String password, int port) {
				prefs.put("user", user);
				prefs.put("password", password);
				prefs.putInt("port", port);

				try {
					controller.configure(port, user, password);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(MainFrame.this, "Unable to re-connect");
				}
			}
		});

		String user = prefs.get("user", "");
		String password = prefs.get("password", "");
		Integer port = prefs.getInt("port", 3306);

		prefsDialog.setDefaults(user, password, port);

		try {
			controller.configure(port, user, password);
		} catch (Exception e1) {
			System.err.println("Can't connect to database");
		}

		fileChooser = new JFileChooser();
		fileChooser.addChoosableFileFilter(new PersonFileFilter());

		setJMenuBar(createMenuBar());

		toolbar.setToolbarListener(new ToolbarListener() {
			public void saveEventOccured() {
				connect();
				try {
					controller.save();
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(MainFrame.this, "Unable to save to database.",
							"Database Connection Problem", JOptionPane.ERROR_MESSAGE);
				}
			}

			public void refreshEventOccured() {
				connect();
				try {
					controller.load();
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(MainFrame.this, "Unable to load from database.",
							"Database Connection Problem", JOptionPane.ERROR_MESSAGE);
				}
				tablePanel.refresh();
			}
		});

		formPanel.setFormListener(new FormListener() {
			public void formEventOccured(FormEvent e) {
				controller.addPerson(e);
				tablePanel.refresh();
			}
		});

		add(toolbar, BorderLayout.PAGE_START);
		add(splitPane, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("Window closing"); // To change body of
														// generated methods,
														// choose Tools |
														// Templates.
				controller.disconnect();
				dispose();
				System.gc();
			}
		});

		setMinimumSize(new Dimension(500, 400));
		setSize(600, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}

	public void connect() {
		try {
			controller.connect();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(MainFrame.this, "Cannot connect to database.", "Database Connection Problem",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public JMenuBar createMenuBar() {

		JMenuBar menuBar = new JMenuBar();

		JMenuItem exportDataItem = new JMenuItem(messages.getString("ExportData"));
		JMenuItem importDataItem = new JMenuItem(messages.getString("ImportData"));
		JMenuItem exitItem = new JMenuItem(messages.getString("Exit"));

		JMenu fileMenu = new JMenu(messages.getString("File"));
		fileMenu.add(exportDataItem);
		fileMenu.add(importDataItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		JMenu windowMenu = new JMenu(messages.getString("Window"));
		JMenu showMenu = new JMenu(messages.getString("Show"));
		JMenuItem prefsItem = new JMenuItem(messages.getString("Preferences"));

		JMenu languageMenu = new JMenu(messages.getString("Language"));
		JMenuItem englishMenu = new JMenuItem(messages.getString("English"), US_Flag);
		JMenuItem frenchMenu = new JMenuItem(messages.getString("French"), French_Flag);

		JCheckBoxMenuItem showFormItem = new JCheckBoxMenuItem(messages.getString("PersonForm"));
		showFormItem.setSelected(true);

		showMenu.add(showFormItem);
		windowMenu.add(showMenu);
		windowMenu.add(prefsItem);

		languageMenu.add(englishMenu);
		languageMenu.add(frenchMenu);

		menuBar.add(fileMenu);
		menuBar.add(windowMenu);
		menuBar.add(languageMenu);

		frenchMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent frButtonClicked) {
				final App app = new App();
				app.frenchMenuState = true;
				languageFr = new String("fr");
				countryFr = new String("FR");
				final Locale currentLocaleFr;
				final ResourceBundle messagesFr;
				currentLocaleFr = new Locale(languageFr, countryFr);
				messagesFr = ResourceBundle.getBundle("ResourceBundle", currentLocaleFr);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						app.mainframe.setVisible(false);
						new MainFrame(currentLocaleFr, messagesFr, languageFr, countryFr);
						WindowListener[] listeners = getWindowListeners();
						for (WindowListener listener : listeners) {
							listener.windowClosing(new WindowEvent(MainFrame.this, 0));
						}
					}
				});
			}
		});

		englishMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent enButtonClicked) {
				final App app = null;
				app.frenchMenuState = false;
				languageEn = new String("en");
				countryEn = new String("US");
				final Locale currentLocaleEn;
				final ResourceBundle messagesEn;
				currentLocaleEn = new Locale(languageEn, countryEn);
				messagesEn = ResourceBundle.getBundle("ResourceBundle", currentLocaleEn);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						app.mainframe.setVisible(false);
						new MainFrame(currentLocaleEn, messagesEn, languageEn, countryEn);
						WindowListener[] listeners = getWindowListeners();
						for (WindowListener listener : listeners) {
							listener.windowClosing(new WindowEvent(MainFrame.this, 0));
						}
					}
				});
			}
		});

		prefsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefsDialog.setVisible(true);
			}
		});

		showFormItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) ev.getSource();

				if (menuItem.isSelected()) {
					splitPane.setDividerLocation((int) formPanel.getMinimumSize().getWidth());
				}
				formPanel.setVisible(menuItem.isSelected());
			}
		});

		fileMenu.setMnemonic(KeyEvent.VK_F);
		// exitItem.setMnemonic(KeyEvent.VK_X);

		prefsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));

		// exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
		// ActionEvent.CTRL_MASK));

		// importDataItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,ActionEvent.CTRL_MASK));

		importDataItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final App app = null;

				if (app.frenchMenuState = true) {
					z_choosertitleImport = messages.getString("ImportTitle");
					UIManager.put("FileChooser.lookInLabelText", messages.getString("lookInLabelText"));
					UIManager.put("FileChooser.filesOfTypeLabelText", messages.getString("filesOfTypeLabelText"));
					UIManager.put("FileChooser.upFolderToolTipText", messages.getString("upFolderToolTipText"));
					UIManager.put("FileChooser.fileNameLabelText", messages.getString("fileNameLabelText"));
					UIManager.put("FileChooser.homeFolderToolTipText", messages.getString("homeFolderToolTipText"));
					UIManager.put("FileChooser.newFolderToolTipText", messages.getString("newFolderToolTipText"));
					UIManager.put("FileChooser.listViewButtonToolTipText", messages.getString("listViewButtonToolTipText"));
					UIManager.put("FileChooser.detailsViewButtonToolTipText", messages.getString("detailsViewButtonToolTipText"));
					UIManager.put("FileChooser.saveButtonText", messages.getString("saveButtonText"));
					UIManager.put("FileChooser.openButtonText", messages.getString("openButtonText"));
					UIManager.put("FileChooser.cancelButtonText", messages.getString("cancelButtonText"));
					UIManager.put("FileChooser.updateButtonText", messages.getString("updateButtonText"));
					UIManager.put("FileChooser.helpButtonText", messages.getString("helpButtonText"));
					UIManager.put("FileChooser.saveButtonToolTipText", messages.getString("saveButtonToolTipText"));
					UIManager.put("FileChooser.openButtonToolTipText", messages.getString("openButtonToolTipText"));
					UIManager.put("FileChooser.cancelButtonToolTipText", messages.getString("cancelButtonToolTipText"));
					UIManager.put("FileChooser.updateButtonToolTipText", messages.getString("updateButtonToolTipText"));
					UIManager.put("FileChooser.helpButtonToolTipText", messages.getString("helpButtonToolTipText"));
					JFileChooser z_chooser = new JFileChooser();
					z_chooser.setDialogTitle(z_choosertitleImport);
					if (z_chooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
						try {
							controller.loadFromFile(z_chooser.getSelectedFile());
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from file.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} 
				else {
					app.frenchMenuState = false;
					if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
						try {
							controller.loadFromFile(fileChooser.getSelectedFile());
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from file.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}

			}
		});

		exportDataItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final App app = null;

				if (app.frenchMenuState = true) {
					z_choosertitleExport = messages.getString("ExportTitle");
					UIManager.put("FileChooser.lookInLabelText", messages.getString("lookInLabelText"));
					UIManager.put("FileChooser.filesOfTypeLabelText", messages.getString("filesOfTypeLabelText"));
					UIManager.put("FileChooser.upFolderToolTipText", messages.getString("upFolderToolTipText"));
					UIManager.put("FileChooser.fileNameLabelText", messages.getString("fileNameLabelText"));
					UIManager.put("FileChooser.homeFolderToolTipText", messages.getString("homeFolderToolTipText"));
					UIManager.put("FileChooser.newFolderToolTipText", messages.getString("newFolderToolTipText"));
					UIManager.put("FileChooser.listViewButtonToolTipText", messages.getString("listViewButtonToolTipText"));
					UIManager.put("FileChooser.detailsViewButtonToolTipText", messages.getString("detailsViewButtonToolTipText"));
					UIManager.put("FileChooser.saveButtonText", messages.getString("saveButtonText"));
					UIManager.put("FileChooser.openButtonText", messages.getString("openButtonText"));
					UIManager.put("FileChooser.cancelButtonText", messages.getString("cancelButtonText"));
					UIManager.put("FileChooser.updateButtonText", messages.getString("updateButtonText"));
					UIManager.put("FileChooser.helpButtonText", messages.getString("helpButtonText"));
					UIManager.put("FileChooser.saveButtonToolTipText", messages.getString("saveButtonToolTipText"));
					UIManager.put("FileChooser.openButtonToolTipText", messages.getString("openButtonToolTipText"));
					UIManager.put("FileChooser.cancelButtonToolTipText", messages.getString("cancelButtonToolTipText"));
					UIManager.put("FileChooser.updateButtonToolTipText", messages.getString("updateButtonToolTipText"));
					UIManager.put("FileChooser.helpButtonToolTipText", messages.getString("helpButtonToolTipText"));
					JFileChooser z_chooser = new JFileChooser();
					z_chooser.setDialogTitle(z_choosertitleExport);
					if (z_chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
					try {
						controller.saveToFile(fileChooser.getSelectedFile());
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(MainFrame.this, "Could not save data to file.", "Error",
								JOptionPane.ERROR_MESSAGE);
							}
						}
					}
					else {
						app.frenchMenuState = false;
						if (fileChooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION) {
							try {
								controller.loadFromFile(fileChooser.getSelectedFile());
							} catch (IOException ex) {
								JOptionPane.showMessageDialog(MainFrame.this, "Could not load data from file.", "Error",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			});

		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final App app = null;
				int action = 0;

				if (app.frenchMenuState = true) {
					UIManager.put("OptionPane.okButtonTextFR", messages.getString("OK"));
					UIManager.put("OptionPane.cancelButtonTextFR", messages.getString("Cancel"));
					Object[] options = { UIManager.get("OptionPane.okButtonTextFR"),
							UIManager.get("OptionPane.cancelButtonTextFR") };
					action = JOptionPane.showOptionDialog(MainFrame.this,
							(messages.getString("DoYouReallyWantToExitTheApplication")),
							(messages.getString("ConfirmExit")), JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
				} else {
					action = JOptionPane.showConfirmDialog(MainFrame.this,
							(messages.getString("DoYouReallyWantToExitTheApplication")),
							(messages.getString("ConfirmExit")), JOptionPane.OK_CANCEL_OPTION);
				}

				if (action == JOptionPane.OK_OPTION) {
					WindowListener[] listeners = getWindowListeners();

					for (WindowListener listener : listeners) {
						listener.windowClosing(new WindowEvent(MainFrame.this, 0));
					}
				}

			}
		});

		return menuBar;
	}
}
