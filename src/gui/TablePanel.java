package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import model.Person;

public class TablePanel extends JPanel {

	private JTable table;
	private PersonTableModel tableModel;
	private JPopupMenu popup;
	private PersonTableListener personTableListener;
	private String language;
	private String country;
	private Locale currentLocale;
	private ResourceBundle messages;

	public TablePanel(Locale currentLocale, ResourceBundle messages,
			String language, String country) {
		this.language = language;
		this.country = country;
		this.currentLocale = currentLocale;
		this.messages = messages;

		tableModel = new PersonTableModel(currentLocale, messages, language,
				country);
		table = new JTable(tableModel);
		popup = new JPopupMenu();

		JMenuItem removeItem = new JMenuItem("Delete row");
		popup.add(removeItem);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				int row = table.rowAtPoint(e.getPoint());
				table.getSelectionModel().setSelectionInterval(row, row);

				if (e.getButton() == MouseEvent.BUTTON3) {
					popup.show(table, e.getX(), e.getY());
				}
			}
		});

		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();

				if (personTableListener != null) {
					personTableListener.rowDeleted(row);
					tableModel.fireTableRowsDeleted(row, row);
				}
			}
		});
		
		setLayout(new BorderLayout());
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void setData(List<Person> db) {
		tableModel.setData(db);
	}

	public void refresh() {
		tableModel.fireTableDataChanged();
	}

	public void setPersonTableListener(PersonTableListener listener) {
		this.personTableListener = listener;
	}
}
