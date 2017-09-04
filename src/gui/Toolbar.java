package gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;

public class Toolbar extends JToolBar implements ActionListener {
	
	private JButton saveButton;
	private JButton refreshButton;
	private JButton LanguageButton;
	private String language;
	private String country;
	private Locale currentLocale;
	private ResourceBundle messages;
	private ToolbarListener textListener;

	public Toolbar(Locale currentLocale, ResourceBundle messages, String language, String country) {
		this.language = language;
		this.country = country;
		this.currentLocale = currentLocale;
		this.messages = messages;
		//Get rid of the Border if you want the Toolbar drugable		
		setBorder(BorderFactory.createEtchedBorder());
		//setFloatable(false);

		saveButton = new JButton();
		saveButton.setIcon(Utils.createIcon("/images/Save.gif"));
		saveButton.setToolTipText(messages.getString("Save"));
		
		refreshButton = new JButton();
		refreshButton.setIcon(Utils.createIcon("/images/Refresh.gif"));
		refreshButton.setToolTipText(messages.getString("Refresh"));
		
		
		saveButton.addActionListener(this);
		refreshButton.addActionListener(this);
		
		add(saveButton);
		//addSeparator();
		add(refreshButton);
	}

	public void setToolbarListener(ToolbarListener listener) {
		this.textListener = listener;
	}

	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();

		if (clicked == saveButton) {
			if (textListener != null) {
				textListener.saveEventOccured();
			}
		} else if (clicked == refreshButton) {
			if (textListener != null) {
				textListener.refreshEventOccured();
			}
		}
		
		}
	}
	
//}
