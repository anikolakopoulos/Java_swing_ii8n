package controller;

import gui.App;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import model.Message;

/* 
 * This is a sort of simulated message server
 */

public class MessageServer implements Iterable<Message> {
	private Map<Integer, List<Message>> messages;

	private List<Message> selected;
	private String language;
	private String country;
	private Locale currentLocale;
	private ResourceBundle messagesI18N;
	private App app;
	private double price;
	private double rate;
	private double priceInEURO;
	private MessageServer messageServer;

	public MessageServer(Locale currentLocale, ResourceBundle messagesI18N, String language, String country) {
		selected = new ArrayList<Message>();
		messages = new TreeMap<Integer, List<Message>>();
		this.language = language;
		this.country = country;
		this.currentLocale = currentLocale;
		this.messagesI18N = messagesI18N;

		if (app.frenchMenuState == true) {

			List<Message> list = new ArrayList<Message>();
			
			double RedHatEnterpriseLinuxServer = 1491.00;
			double RedHatEnterpriseLinuxServerEntryLevel = 541.00;
			double RedHatEnterpriseLinuxforVirtualDatacenters = 3825.00;
			double RedHatEnterpriseLinuxServerforIBMPOWER = 8600.00;
			double RedHatEnterpriseLinuxforIBMSystemz = 18000.00;
			double WindowsServer2012R2Datacenter = 6155.00;
			double WindowsServer2012R2Standard = 882.00;
			double WindowsServer2012R2Essentials = 501.00;
			double MySQLStandardEditionSubscription = 2000.00;
			double MySQLEnterpriseEditionSubscription = 5000.00;
			double MySQLClusterCarrierGradeEditionSubscription = 5000.00;
									
			String totalRedHatServer = showPriceInEURO(RedHatEnterpriseLinuxServer, getExchangeRate("EURO"));
		    String totalRedHatServerList = "Red Hat Enterprise Linux Server: " + totalRedHatServer;
			list.add(new Message(totalRedHatServerList, "$1,491"));
			list.get(0).getTitle();
			String totalRedHatEntry = showPriceInEURO(RedHatEnterpriseLinuxServerEntryLevel, getExchangeRate("EURO"));
		    String totalRedHatEntryList = "Red Hat Enterprise Linux Server Entry Level: " + totalRedHatEntry;
			list.add(new Message(totalRedHatEntryList, "$541"));
			list.get(1).getTitle();		
			String totalRedHatDatacenters = showPriceInEURO(RedHatEnterpriseLinuxforVirtualDatacenters, getExchangeRate("EURO"));
		    String totalRedHatDatacentersList = "Red Hat Enterprise Linux for Virtual Datacenters: " + totalRedHatDatacenters;
			list.add(new Message(totalRedHatDatacentersList, "$3,825"));
			list.get(2).getTitle();	
			String totalRedHatIBMPOWER = showPriceInEURO(RedHatEnterpriseLinuxServerforIBMPOWER, getExchangeRate("EURO"));
		    String totalRedHatIBMPOWERList = "Red Hat Enterprise Linux Server for IBM POWER: " + totalRedHatIBMPOWER;
			list.add(new Message(totalRedHatIBMPOWERList, "$8,600"));
			list.get(3).getTitle();	
			String totalRedHatIBMSystemz = showPriceInEURO(RedHatEnterpriseLinuxforIBMSystemz, getExchangeRate("EURO"));
		    String totalRedHatIBMSystemzList = "Red Hat Enterprise Linux for IBM System z: " + totalRedHatIBMSystemz;
			list.add(new Message(totalRedHatIBMSystemzList, "$18,000"));
			list.get(4).getTitle();
			messages.put(0, list);

			list = new ArrayList<Message>();
			String total2012R2Datacenter = showPriceInEURO(WindowsServer2012R2Datacenter, getExchangeRate("EURO"));
		    String total2012R2DatacenterList = "Windows Server 2012 R2 Datacenter: " + total2012R2Datacenter;
			list.add(new Message(total2012R2DatacenterList, "$6,155"));
			list.get(0).getTitle();
			String total2012R2Standard = showPriceInEURO(WindowsServer2012R2Standard, getExchangeRate("EURO"));
		    String total2012R2StandardList = "Windows Server 2012 R2 Standard: " + total2012R2Standard;
			list.add(new Message(total2012R2StandardList, "$882"));
			list.get(1).getTitle();
			String total2012R2Essentials = showPriceInEURO(WindowsServer2012R2Essentials, getExchangeRate("EURO"));
		    String total2012R2EssentialsList = "Windows Server 2012 R2 Essentials: " + total2012R2Essentials;
			list.add(new Message(total2012R2EssentialsList, "$501"));
			list.get(2).getTitle();
			messages.put(1, list);

			list = new ArrayList<Message>();
			String totalMySQLStandard = showPriceInEURO(MySQLStandardEditionSubscription, getExchangeRate("EURO"));
		    String totalMySQLStandardList = "MySQL Standard Edition Subscription: " + totalMySQLStandard;
			list.add(new Message(totalMySQLStandardList, "$2,000"));
			list.get(0).getTitle();
			String totalMySQLEnterprise = showPriceInEURO(MySQLEnterpriseEditionSubscription, getExchangeRate("EURO"));
		    String totalMySQLEnterpriseList = "MySQL Enterprise Edition Subscription: " + totalMySQLEnterprise;
			list.add(new Message(totalMySQLEnterpriseList, "$5,000"));
			list.get(1).getTitle();
			String totalMySQLClusterCarrierGrade = showPriceInEURO(MySQLClusterCarrierGradeEditionSubscription, getExchangeRate("EURO"));
		    String totalMySQLClusterCarrierGradeList = "MySQL Cluster Carrier Grade Edition Subscription: " + totalMySQLClusterCarrierGrade;
			list.add(new Message(totalMySQLClusterCarrierGradeList, "$10,000"));
			list.get(2).getTitle();
			messages.put(3, list);
		}
		else {
			List<Message> list = new ArrayList<Message>();
			
			list.add(new Message("Red Hat Enterprise Linux Server: $1,491", "$1,491"));
			list.add(new Message("Red Hat Enterprise Linux Server Entry Level: $541", "$541"));
			list.add(new Message("Red Hat Enterprise Linux for Virtual Datacenters: $3,825", "$3,825"));
			list.add(new Message("Red Hat Enterprise Linux Server for IBM POWER: $8,600", "$8,600"));
			list.add(new Message("Red Hat Enterprise Linux for IBM System z: $18,000", "$18,000"));
			messages.put(0, list);

			list = new ArrayList<Message>();
			list.add(new Message("Windows Server 2012 R2 Datacenter: $6,155", "$6,155"));
			list.add(new Message("Windows Server 2012 R2 Standard: $882", "$882"));
			list.add(new Message("Windows Server 2012 R2 Essentials: $501", "$501"));
			messages.put(1, list);

			list = new ArrayList<Message>();
			list.add(new Message("MySQL Standard Edition Subscription: $2,000", "$2,000"));
			list.add(new Message("MySQL Enterprise Edition Subscription: $5,000", "$5,000"));
			list.add(new Message("MySQL Cluster Carrier Grade Edition Subscription: $10,000", "$10,000"));
			messages.put(3, list);
		}
		
	}

	public void showPriceInUSD(double price, double rate) {
		double priceInUSD = price * rate;
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
		System.out.printf("Price in USD : %s %n", currencyFormat.format(priceInUSD));
	}

	public static String showPriceInEURO(double price, double rate) {
		double priceInEURO = price * rate;
		NumberFormat EURO = NumberFormat.getCurrencyInstance(Locale.FRANCE);
		System.out.printf(" %s %n", EURO.format(priceInEURO));
		return EURO.format(priceInEURO);
	}

	public static double getExchangeRate(String currency) {
		switch (currency) {
		case "USD":
			return 1;
		case "EURO":
			return 0.73;
		default:
			throw new IllegalArgumentException(String.format("No rates available for currency %s %n", currency));
		}
	}

	public void setSelectedServers(Set<Integer> servers) {

		selected.clear();
		for (Integer id : servers) {
			if (messages.containsKey(id)) {
				selected.addAll(messages.get(id));
			}
		}
	}

	public int getMessageCount() {
		return selected.size();
	}

	@Override
	public Iterator<Message> iterator() {
		return new MessageIterator(selected);
	}
}

class MessageIterator implements Iterator {

	private Iterator<Message> iterator;

	public MessageIterator(List<Message> messages) {
		iterator = messages.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Object next() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
		return iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

}
