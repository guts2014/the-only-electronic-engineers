package Model;

import java.util.ArrayList;

import com.bloomberglp.blpapi.Session;

/**
 * Creates connection and perform query ready for the GUI
 * @SecurityDataRetrieval
 * @SecrurityLookup
 * @author laurenastell
 *
 */
public class QuerySearch {
	
	private ArrayList<ArrayList<String>> dataSet;
	private String title;

	public QuerySearch(String query) {
		//create connection 
		CreateConnection con = new CreateConnection();

		Session session = con.getSession();

		//create dataset by entering into the lookup class and wanting to return only 1 result
		SecurityLookup sl = new SecurityLookup(session, query, 1);
		//This also sets the date in the format yyyymmdd
		SecurityDataRetrieval data = new SecurityDataRetrieval(session, sl.getSymbols());
		ArrayList<ArrayList<ArrayList<String>>> dData = data.getDataValues();
		
		this.dataSet = dData.get(0); //gets the data from the first result
		this.title = sl.getSymbols().get(0).get(1); //the description from the first result

	}
	
	public ArrayList<ArrayList<String>> getDataSet() {
		return this.dataSet;
	}

	public String getTitle() {
		return this.title;
	}
	
}
