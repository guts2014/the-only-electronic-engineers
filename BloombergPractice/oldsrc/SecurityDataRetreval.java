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


import java.util.ArrayList;

import com.bloomberglp.blpapi.Element;
import com.bloomberglp.blpapi.Event;
import com.bloomberglp.blpapi.Message;
import com.bloomberglp.blpapi.MessageIterator;
import com.bloomberglp.blpapi.Request;
import com.bloomberglp.blpapi.Service;
import com.bloomberglp.blpapi.Session;


public class ServicesFind
{
	/**
	 * @param args
	 */

	private Session session;
	private ArrayList<ArrayList<String>>  symbols;
	private ArrayList<ArrayList<ArrayList<String>>> dataValues;

	public ServicesFind(Session session, ArrayList<ArrayList<String>> symbols) {
		this.session = session;
		this.symbols = symbols;

		this.dataValues = new ArrayList<ArrayList<ArrayList<String>>>();


		run();
	}

	public ArrayList<ArrayList<ArrayList<String>>> getDataValues() { 
		return this.dataValues;
	}

	private void run() {
		
		for (ArrayList<String> al : symbols) {
			boolean first = true;
			for (String s : al) {
				s = s.replace("<", " ");
				s = s.replace(">", "");
				if (first) {	
					getData(s);
					first = false;
				}
			}
		}
	}






	private void getData(String value)  {

		try {

			//Queries begin
			Service refDataService = session.getService("//blp/refdata");
			Request request = refDataService.createRequest("HistoricalDataRequest");
			Element securities = request.getElement("securities");
			securities.appendValue(value);

			Element fields = request.getElement("fields");
			fields.appendValue("PX_LAST");

			request.set("periodicityAdjustment", "ACTUAL");
			request.set("periodicitySelection", "MONTHLY");
			request.set("startDate", "20060101");
			request.set("endDate", "20141231");
			request.set("maxDataPoints", 100);
			request.set("returnEids", true);

			session.sendRequest(request, null);



			//Variables used in the while loop
			Element HistDataResponse;
			Element secData;
			Element fieldDataArray;
			Element fieldData; 
			Element field;
			ArrayList<String> tempArray; 
			ArrayList<ArrayList<String>> dataVal;


			/**
			 * Gets the value of the symbol over a time period and stores it in the arrayList data. 
			 */
			while (true) {
				Event event = session.nextEvent();
				MessageIterator msgIter = event.messageIterator();

				//Loops through each section of data (the array details, with the data values)
				while (msgIter.hasNext()) {
					Message msg = msgIter.next();

					//Selects the array we want fieldDataArray, by going through other elements
					HistDataResponse = msg.asElement();
					secData = HistDataResponse.getElement("securityData");
					fieldDataArray = secData.getElement("fieldData");
					
					//initalise arrayList
					dataVal = new ArrayList<ArrayList<String>>();

					//Loops through the fieldData array
					int numItems = fieldDataArray.numValues();
					for (int i = 0; i < numItems; i++){
						fieldData = fieldDataArray.getValueAsElement(i);
						//Loops through each element of fieldData
						for (int j = 0; j < fieldData.numValues(); j++) {
						
							tempArray = new ArrayList<String>();
							
							field = fieldData.getElement(j);
							tempArray.add(j, field.getValueAsString());
							
							field = fieldData.getElement(++j);
							tempArray.add(j, field.getValueAsString());
							
							dataVal.add(tempArray);
						}
						
						dataValues.add(dataVal);
					}
				}
				if (event.eventType() == Event.EventType.RESPONSE) {
					break;
				}
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

}

