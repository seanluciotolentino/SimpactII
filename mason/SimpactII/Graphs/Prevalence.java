/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Graphs;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author visiting_researcher
 */
public class Prevalence extends JFrame{
    
    private int timeGranularity = 4;
    
    public Prevalence(SimpactII state){
        super("Prevalence and Incidence");
        
        //create data
        XYSeries prevData = new XYSeries("Prevalence");
        XYSeries inciData = new XYSeries("Incidence");
        int numAgents = state.myAgents.size(); //this were added to this data struct, but should not have been remove.
        double now = Math.min(state.numberOfYears*52, state.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        double population;
        double totalInfections;
        double weekInfections;
        for (int t = timeGranularity; t < now; t += timeGranularity) { //Go through each time step (skipping the first)
            //at each time step collect ALIVE POPULATION and NUMBER INFECTED
            population = 0;
            totalInfections = 0;
            weekInfections = 0;
            for (int i = 0; i < numAgents; i++) { 
                Agent agent = (Agent) state.myAgents.get(i);
                
                if (agent.timeOfAddition < t && agent.timeOfRemoval > t){
                    population++;
                    if (agent.weeksInfected>=1 && (now-agent.weeksInfected)< t) //you are (1) infected AND (2) infected before time t
                        totalInfections++; 
                    if ( (now-agent.weeksInfected) == t) //you were infected this week
                        weekInfections++; 
                }
            }
            
            //after everyone for this time step, add to the data
            //System.out.println("adding: " + t + " , " + totalInfections / population);
            prevData.add( t , totalInfections / population );
            inciData.add( t , weekInfections );
        }
        
        //this is an odd bit of [necessary] code. 
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(prevData);
        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(inciData);
        
        //create the prevalence chart
        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "Prevalence", 
                "Time",
                "Prevalence (%)",
                dataset1,
                PlotOrientation.VERTICAL,
                false,   //legend
                false,   //tooltips
                false); //urls
        
        XYPlot plot = (XYPlot) chart1.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.black);        
        plot.setDomainGridlinePaint(Color.black);  
        ChartPanel chartPanel1 = new ChartPanel(chart1);
        
        //create the incidence chart
        JFreeChart chart2 = ChartFactory.createXYLineChart(
                "Incidence", 
                "Time",
                "Incidence (Counts)",
                dataset2,
                PlotOrientation.VERTICAL,
                false,   //legend
                false,   //tooltips
                false); //urls
        
        XYPlot plot2 = (XYPlot) chart2.getPlot();
        plot2.setBackgroundPaint(Color.WHITE);
        plot2.setRangeGridlinePaint(Color.black);        
        plot2.setDomainGridlinePaint(Color.black);  
        ChartPanel chartPanel2 = new ChartPanel(chart2);
        
        //silly GUI stuff       
        JPanel chartPanel = new JPanel();
        chartPanel.add(chartPanel1);
        chartPanel.add(chartPanel2);
        setContentPane(chartPanel);
        
        pack();
        setVisible(true);
    }
    
}
