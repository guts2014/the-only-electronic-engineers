
/*
 * Copyright 2012. Bloomberg Finance L.P.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:  The above
 * copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Identity;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Name;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;

import java.util.ArrayList;

/** Adapted from Bloomberg's example: SecurityLookupExample
 * This allows the user to enter in a string input and receive an array list of related symbols and descriptions
 * This can be used to perform queries using the API
 * 
 * @author laurenastell
 *
 */
public class SecurityLookup
{

	private static final Name SESSION_TERMINATED = Name.getName("SessionTerminated");
	private static final Name SESSION_FAILURE = Name.getName("SessionStartupFailure");
	private static final Name DESCRIPTION_ELEMENT = Name.getName("description");
	private static final Name QUERY_ELEMENT = Name.getName("query");
	private static final Name RESULTS_ELEMENT = Name.getName("results");
	private static final Name MAX_RESULTS_ELEMENT = Name.getName("maxResults");

	private static final Name SECURITY_ELEMENT = Name.getName("security");

	private static final Name ERROR_RESPONSE = Name.getName("ErrorResponse");
	private static final Name INSTRUMENT_LIST_RESPONSE = Name.getName("InstrumentListResponse");
	private static final Name INSTRUMENT_LIST_REQUEST = Name.getName("instrumentListRequest");

	private static final String INSTRUMENT_SERVICE = "//blp/instruments";
	private static final int DEFAULT_MAX_RESULTS = 10;

	private String d_queryString;
	private Name d_requestType = INSTRUMENT_LIST_REQUEST;
	private int d_maxResults = DEFAULT_MAX_RESULTS;

	private Session session;

	private ArrayList<ArrayList<String>> symbolIDs ;

	/**
	 * Uses the default max return values
	 * @param session - Current session connected to the VPN
	 * @param queryParam -String that is to be searched
	 */
	public SecurityLookup(Session session, String queryParam) {

		//set the fields
		this.session = session;
		this.d_queryString = queryParam;

		//Initializes the arrayList
		this.symbolIDs = new ArrayList<ArrayList<String>> ();

		run();
	}

	/**
	 * Allows the user to set the number of maxinum values that will be returned
	 * @param session
	 * @param queryParam
	 * @param maxResults
	 */
	public SecurityLookup(Session session, String queryParam, int maxResults) {

		//set the fields
		this.session = session;
		this.d_queryString = queryParam;
		this.d_maxResults = maxResults;

		//Initializes the arrayList
		this.symbolIDs = new ArrayList<ArrayList<String>> ();

		run();
	}


	//Returns the result produced when running the query
	public ArrayList<ArrayList<String>> getSymbols() {
		return this.symbolIDs;
	}

	/**
	 * This sets up the enviroment to call from the API 
	 */
	private void run() {
		try {
			Identity identity = session.createIdentity();

			if (!session.openService(INSTRUMENT_SERVICE)) {
				System.err.println("Failed to open " + INSTRUMENT_SERVICE);
				return;
			}

			sendRequest(session, identity);
			eventLoop(session);
		}
		catch (Exception e) {
			System.err.printf("Exception: %1$s\n", e.getMessage());
			System.err.println();
		}
	}


	/**
	 * Loops through the results and allows the results to be both displayed and added to the 
	 * arrayList
	 * @param msg
	 */
	private void processInstrumentListResponse(Message msg) {
		//temp ArrayList
		ArrayList<String> temp;
		Element results = msg.getElement(RESULTS_ELEMENT);
		int numResults = results.numValues();
		System.out.println("Processing " + numResults + " results:");
		for (int i = 0; i < numResults; ++i) {
			Element result = results.getValueAsElement(i);
			System.out.printf(
					"\t%1$d %2$s - %3$s\n",
					i+1,
					result.getElementAsString(SECURITY_ELEMENT),
					result.getElementAsString(DESCRIPTION_ELEMENT));

			//create and fill temp array to be added to symbolsID
			temp = new ArrayList<String>();
			temp.add(result.getElementAsString(SECURITY_ELEMENT));
			temp.add(result.getElementAsString(DESCRIPTION_ELEMENT));

			this.symbolIDs.add(i, temp);		
		}
	}


	private void processResponseEvent(Event event) {
		MessageIterator msgIter = event.messageIterator();
		while (msgIter.hasNext()) {
			Message msg = msgIter.next();
			if (msg.messageType() == ERROR_RESPONSE) {
				String description = msg.getElementAsString(DESCRIPTION_ELEMENT);
				System.out.println("Received error: " + description);
			}
			else if (msg.messageType() == INSTRUMENT_LIST_RESPONSE) {
				processInstrumentListResponse(msg);
			}
			else {
				System.err.println("Unknown MessageType received");
			}
		}
	}

	private void eventLoop(Session session) throws InterruptedException {
		boolean done = false;
		while (!done) {
			Event event = session.nextEvent();
			if (event.eventType() == Event.EventType.PARTIAL_RESPONSE) {
				System.out.println("Processing Partial Response");
				processResponseEvent(event);
			}
			else if (event.eventType() == Event.EventType.RESPONSE) {
				System.out.println("Processing Response");
				processResponseEvent(event);
				done = true;
			}
			else {
				MessageIterator msgIter = event.messageIterator();
				while (msgIter.hasNext()) {
					Message msg = msgIter.next();
					System.out.println(msg.asElement());
					if (event.eventType() == Event.EventType.SESSION_STATUS) {
						if (msg.messageType() == SESSION_TERMINATED
								|| msg.messageType() == SESSION_FAILURE) {
							done = true;
						}
					}
				}
			}
		}
	}

	private void sendRequest(Session session, Identity identity) throws Exception {
		System.out.println("Sending Request: " + d_requestType.toString());
		Service instrumentService = session.getService(INSTRUMENT_SERVICE);
		Request request = instrumentService.createRequest(d_requestType.toString());

		request.set(QUERY_ELEMENT, d_queryString);
		request.set(MAX_RESULTS_ELEMENT, d_maxResults);

		System.out.println(request);
		session.sendRequest(request, identity, null);
	}
}