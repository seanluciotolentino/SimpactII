/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Graphs;

import SimpactII.Relationship;
import SimpactII.SimpactII;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author visiting_researcher
 */
public class FormedRelations extends JFrame {

    private int timeGranularity = 4; //4 = 1 month, 52 = 1 year, etc...    

    public FormedRelations(SimpactII state) {
        super("Formed Relationship");

        //PART 1: by relationship ID and duration
        XYSeriesCollection allRelations = new XYSeriesCollection();
        int numRelations = state.allRelations.size();

        //PART 2: Number of relationships over time
        XYSeries data = new XYSeries("Number of Relations");
        double now = Math.min(state.numberOfYears * 52, state.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        int timesteps = (int) now / timeGranularity;
        int[] countSeries = new int[timesteps];
        
        //go through relations and add appropriate information
        for (int i = 0; i < numRelations; i++) { 
            Relationship r = (Relationship) state.allRelations.get(i);

            //--PART 1 ALL RELATIONSHIPS SECTION--
            XYSeries thisRelationship = new XYSeries("Relation " + i);
            thisRelationship.add(i, r.getStart());
            thisRelationship.add(i, r.getEnd());
            allRelations.addSeries(thisRelationship);//...and add series to collection

            //--PART 2 NUMBER RELATIONSHIPS SECTION--
            int start = (int) r.getStart() / timeGranularity;
            int end = (int) Math.min(now, r.getEnd()) / timeGranularity; //cutoff any relationships that aren't over by the end of the simulation
            for (int j = start; j < end; j++)
                countSeries[j]++;            
        }
        //--PART 2 CONT...
        for (int t = 0; t < timesteps; t++) //add each of the count series to the data set
            data.add(t, countSeries[t]);        
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data);

        //CHART1
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Formed Relations",
                "Relation ID",
                "Duration [weeks]",
                allRelations,
                PlotOrientation.VERTICAL,
                false, //legend
                false, //tooltips
                false); //urls
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.black);
        plot.setDomainGridlinePaint(Color.black);
        ChartPanel chartPanel1 = new ChartPanel(chart);

        //CHART2
        JFreeChart chart2 = ChartFactory.createXYLineChart(
                "Number of Relations",
                "Time [weeks]",
                "Number of Relationships",
                dataset,
                PlotOrientation.VERTICAL,
                false, //legend
                false, //tooltips
                false); //urls
        ChartPanel chartPanel2 = new ChartPanel(chart2);

        //Put it all together:
        JPanel chartPanel = new JPanel();
        chartPanel.add(chartPanel1);
        chartPanel.add(chartPanel2);

        super.setContentPane(chartPanel);
        super.pack();
        super.setVisible(true);



    }
}
