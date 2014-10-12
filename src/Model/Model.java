package Model;

import java.util.ArrayList;
import java.util.Scanner;

import GUI.DateLineGraph;

import com.bloomberglp.blpapi.Session;


/**
 *
 * @author laurenastell
 *
 */
public class Model {
	
	private static ArrayList<ArrayList<String>> dataOptions;

	public static void main(String[] args) {

		//to hold all the different search queries we want to run
		dataOptions = getOptions();

		//displays chart options 

		//open input stream 
		Scanner input = new Scanner(System.in);
		int userInput;
		
		displayOptions();
		
		System.out.println("Welcome!! To use enter either the index of the chart you want to see or 999 for help");
		System.out.println("Help will let you display the graph descriptions again");
		System.out.println("Which graph would you like to see? (For help enter: 999)");
		
		//Loop until the user ends with -1
		while ((userInput = input.nextInt()) != -1) {

			//display graph 
			if (userInput < dataOptions.size()) {

				//checks if the graph is one or two line displays
				String s = dataOptions.get(userInput).get(2);
				if (s == null) {
					//only one series required
					String query = dataOptions.get(userInput).get(1);
					
					//perform the search
					QuerySearch q = new QuerySearch(query);
					
					//view the graph
					DateLineGraph.display(q.getTitle(),q.getDataSet());
				}
				else {
					//two series
					String title = dataOptions.get(userInput).get(0);
					String query1 = dataOptions.get(userInput).get(1);
					String query2 = dataOptions.get(userInput).get(2);
					
					
					//preform search
					QuerySearch q1 = new QuerySearch(query1);
					QuerySearch q2 = new QuerySearch(query2);
					
					//view the graph
					DateLineGraph.display2Axes(title, q1.getDataSet(), q2.getDataSet());
				}
			}
			
			//displayHelp 
			if (userInput == 999) 
				displayHelp();
			
			System.out.println("Which graph would you like to see? (For help enter: 999)");

		}

		//close the scanner when the loop is finished
		input.close();

	}

	private static ArrayList<ArrayList<String>> getOptions() {
		ArrayList<ArrayList<String>> options = new ArrayList<ArrayList<String>>();
		
		//to store the initiale values
		ArrayList<String> temp;
		
		//add a query 
		temp = new ArrayList<String>();
		temp.add(0, "Cheesecake Factory total Tweets and Stock Prices");
		temp.add(1, "cheesecake total tweets");
		temp.add(2, "cheesecake factory");
		
		options.add(0, temp);
		
		//add a query
		temp = new ArrayList<String>();
		temp.add(0, "Google Share Prices");
		temp.add(1, "Google Inc CLASS C");
		temp.add(2, "Google Inc CLASS A");
		
		options.add(1, temp);
		
		temp = new ArrayList<String>();
		temp.add(0, "Got a raise and Got fired Tweets");
		temp.add(1, "Daily Count of Original Tweets Including the String Got a Raise");
		temp.add(2, "Daily Count of Original Tweets Including the String Lost My Job");
		
		options.add(2, temp);
		
		temp = new ArrayList<String>();
		temp.add(0, "RBS Stock Price");
		temp.add(1,"RBS LN");
		temp.add(2, null);
		
		options.add(3,temp);
		
		temp = new ArrayList<String>();
		temp.add(0, "New York vs Glasgow Temperatures");
		temp.add(1, "custom weather new york");
		temp.add(2, "custom weather glasgow");
		
		options.add(4, temp);
		
		temp = new ArrayList<String>();
		temp.add(0, "New York to London Vs London to New York Index");
		temp.add(1, "BBBRNYLO");
		temp.add(2, null);
		
		options.add(5, temp);

		return options;

	}

	/**
	 * Displays the descriptions associated with dataOptions
	 * @param options
	 */
	private static void displayOptions() {
		System.out.printf("%5s %s \n", "Index", "Description");
		for (int i = 0; i < dataOptions.size(); i++) {
			System.out.printf("%5s %s \n", i, dataOptions.get(i).get(0));
		}
	}

	/**
	 * Display the different enrties that the user can input
	 */
	private static void displayHelp() {
		System.out.println("To display the graphs enter the index number. /n Here are the different graphs again:");
		displayOptions();
	}




}
