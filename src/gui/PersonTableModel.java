package gui;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import model.Person;

public class PersonTableModel extends AbstractTableModel {
	
	private String language;
	private String country;
	private Locale currentLocale;
	private ResourceBundle messages;
	
	private List<Person> db;
    private String[] colNames;
	
    public PersonTableModel(Locale currentLocale, ResourceBundle messages, String language, String country) {
    	this.language = language;
		this.country = country;
		this.currentLocale = currentLocale;
		this.messages = messages;
		colNames = new String[] {"ID", messages.getString("Name"), "Occupation", messages.getString("Age_Category"), messages.getString("Employment_Category"), messages.getString("USCitizen"), messages.getString("Tax_ID"), "Gender"};
    }

    @Override
    public String getColumnName(int column) {

        return colNames[column]; //To change body of generated methods, choose Tools | Templates.
    }

    public void setData(List<Person> db) {
        this.db = db;
    }

    public int getRowCount() {
        return db.size();
    }

    public int getColumnCount() {
        return 8;
    }

    public Object getValueAt(int row, int col) {
        Person person = db.get(row);

        switch (col) {
            case 0:
                return person.getId();
            case 1:
                return person.getName();
            case 2:
                return person.getOccupation();
            case 3:
                return person.getAgeCategory();
            case 4:
                return person.getEmpCat();
            case 5:
                return person.isUsCitizen();
            case 6:
                return person.getTaxId();
            case 7:
                return person.getGender();
        }

        return null;
    }
}
