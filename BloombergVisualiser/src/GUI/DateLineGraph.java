package GUI;


/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 */

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import Model.Model;

/**
 * This displays a graph with dates along the X-axis
 * @author laurenastell
 *
 */
public class DateLineGraph  extends JFrame {

	private static final long serialVersionUID = 8758388850863422245L;
	private String chartTitle;
	private ArrayList<ArrayList<String>> dataSet;
	private ArrayList<ArrayList<String>> dataSet2;
	private boolean series2;

	/**
	 * Creates a new demo.
	 *
	 * @param title  the frame title.
	 */
	public DateLineGraph (String title, String chartTitle, ArrayList<ArrayList<String>> data, ArrayList<ArrayList<String>> data2, Boolean series2) {
		super(title);
		
		this.chartTitle = chartTitle;
		this.dataSet = data;
		this.dataSet2 = data2;
		this.series2 = series2;

		final XYDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(875, 473));
		chartPanel.setFillZoomRectangle(true);
		chartPanel.setMouseWheelEnabled(true);
		setContentPane(chartPanel);
	}

	/**
	 * Creates a dataset, consisting of one series of doubles.
	 *
	 * @return the dataset.
	 */
	private XYDataset createDataset() {
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		final TimeSeries series1 = new TimeSeries(chartTitle.split(" ")[0]);
		
		//Creates a series from the data
		for (ArrayList<String> al: dataSet) {
			String [] date = al.get(0).split("-");
			series1.add(new Day(Integer.parseInt(date[2]), Integer.parseInt(date[1]), 
					Integer.parseInt(date[0])), Float.parseFloat(al.get(1)));
			
		}
		
		//Adds the new series to the dataset
		dataset.addSeries(series1);
		
		//If a second line needs to be plotted
		if (this.series2 = true) {
			
			//Creates a second series and adds to the dataset
			final TimeSeries series2 = new TimeSeries(chartTitle.split(" ")[0]);
			
			for (ArrayList<String> al: dataSet2) {
				String [] date = al.get(0).split("-");
				series2.add(new Day(Integer.parseInt(date[2]), Integer.parseInt(date[1]), 
						Integer.parseInt(date[0])), Float.parseFloat(al.get(1)));
			}
			
			dataset.addSeries(series2);		}

		return dataset;
	}

	/**
	 * Creates a chart.
	 * 
	 * @param dataset  the data for the chart.
	 * 
	 * @return a chart.
	 */
	private JFreeChart createChart(final XYDataset dataset) {
		// create the chart...
		final JFreeChart chart = ChartFactory.createTimeSeriesChart(
				this.chartTitle,      // chart title
				"Day",                      // x axis label
				"",                      // y axis label
				dataset,                  // data
				true,                     // include legend
				true,                     // tooltips
				false                     // urls
				);

		chart.setBackgroundPaint(Color.white);

		final XYPlot plot = (XYPlot) chart.getPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
		plot.setBackgroundPaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);
		renderer.setSeriesShapesVisible(1, true);
		plot.setRenderer(renderer);

		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		return chart;

	}

	// ****************************************************************************
	// * JFREECHART DEVELOPER GUIDE                                               *
	// * The JFreeChart Developer Guide, written by David Gilbert, is available   *
	// * to purchase from Object Refinery Limited:                                *
	// *                                                                          *
	// * http://www.object-refinery.com/jfreechart/guide.html                     *
	// *                                                                          *
	// * Sales are used to provide funding for the JFreeChart project - please    * 
	// * support us so that we can continue developing free software.             *
	// ****************************************************************************

	/**
	 * Starting point for the demonstration application.
	 *
	 * @param args  ignored.
	 */
	public static void display(String chartTitle, ArrayList<ArrayList<String>> data) {
		final DateLineGraph xyScatter = new DateLineGraph("Bloomberg Visualiser", chartTitle, data, null, false);
		xyScatter.pack();
		RefineryUtilities.centerFrameOnScreen(xyScatter);
		xyScatter.setVisible(true);
	}
	
	public static void display2Axes(String chartTitle, ArrayList<ArrayList<String>> data, ArrayList<ArrayList<String>> data2) {
		final DateLineGraph xyScatter = new DateLineGraph("Bloomberg Visualiser", chartTitle, data, data2, true);
		xyScatter.pack();
		RefineryUtilities.centerFrameOnScreen(xyScatter);
		xyScatter.setVisible(true);
	}
}