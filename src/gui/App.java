package gui;




import gui.MainFrame;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;

public class App {
	
	static MainFrame mainframe;
	public static boolean frenchMenuState = false;

	public static void main(String[] args) {
		
		final String language;
		final String country;
						
		if (args.length != 2) {
			language = new String("en");
			country = new String("US");
			frenchMenuState = false;
		} else {
			language = new String(args[0]);
			country = new String(args[1]);
		}

		final Locale currentLocale;
		final ResourceBundle messages;
		
		currentLocale = new Locale(language, country);

		messages = ResourceBundle.getBundle("ResourceBundle", currentLocale);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				mainframe = new MainFrame(currentLocale, messages, language, country);
				// new MainFrame();
			}
		});
	}
}
