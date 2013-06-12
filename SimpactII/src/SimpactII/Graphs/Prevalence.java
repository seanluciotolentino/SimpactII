package SimpactII.Graphs;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

/**
 *
 * @author Lucio Tolentino 
 * 
 * Class to generate the Prevalence and Incidence graphs.  This is a dual graph.
 * The first graph shows the proportion of individuals infected with HIV over time,
 * the second graph shows the number of cases each week.
 * 
 */
public class Prevalence extends JFrame{
    
    private int timeGranularity = 4;
    
    public Prevalence(SimpactII state){
        super("Prevalence and Incidence");
        
        //create data
        XYSeries prevData = new XYSeries("Prevalence");
        XYSeries malePrev = new XYSeries("Male Prevalence");
        XYSeries femalePrev = new XYSeries("Female Prevalence");
        XYSeries inciData = new XYSeries("Incidence");
        int numAgents = state.myAgents.size(); //this were added to this data struct, but should not have been remove.
        double now = Math.min(state.numberOfYears*52, state.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        double population;
        double malePopulation;
        double femalePopulation;
        double totalInfections;        
        double maleInfections;
        double femaleInfections;
        double weekInfections;
        for (int t = timeGranularity; t < now; t += timeGranularity) { //Go through each time step (skipping the first)
            //at each time step collect ALIVE POPULATION and NUMBER INFECTED
            population = 0;
            malePopulation = 0;
            femalePopulation = 0;
            
            totalInfections = 0;
            maleInfections = 0;
            femaleInfections = 0;
            
            weekInfections = 0; //for incidence
            
            //go through agents and tally
            for (int i = 0; i < numAgents; i++) { 
                Agent agent = (Agent) state.myAgents.get(i);
                double timeOfInfection = (now - agent.weeksInfected);
                
                if (agent.getTimeOfAddition() < t && agent.timeOfRemoval > t //if he or she is alive at this time step
                        && (agent.age>=15 && agent.age<50 ) ){ //AND within the right age range
                    //add him or her to population counts
                    population++;                    
                    if(agent.isMale() ) {  malePopulation++; }
                    else { femalePopulation++; }
                    
                    //tally him or her for prevalence counts
                    if (agent.weeksInfected>=1 && timeOfInfection < t){ //you are (1) infected AND (2) infected before time t
                        totalInfections++; 
                        if(agent.isMale() ){ maleInfections++; }
                        else { femaleInfections++; }
                    }
                    
                    //tally him or her for incidence counts
                    if ( timeOfInfection > (t - timeGranularity) && timeOfInfection <= t) //you were infected this week
                        weekInfections++; 
                    }
                }
            
            //after everyone for this time step, add to the data
            //System.out.println("adding: " + t + " , " + totalInfections / population);
            prevData.add( t/52.0 , totalInfections / population );
            malePrev.add( t/52.0, maleInfections / malePopulation );
            femalePrev.add( t/52.0, femaleInfections / femalePopulation );
            inciData.add( t/52.0 , weekInfections );
        }
        
        //this is an odd bit of [necessary] code. 
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(prevData);
        dataset1.addSeries(malePrev);
        dataset1.addSeries(femalePrev);
        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(inciData);
        
        //create the prevalence chart
        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "Prevalence", 
                "Time (years)",
                "Prevalence (%)",
                dataset1,
                PlotOrientation.VERTICAL,
                true,   //legend
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
                "Time (years)",
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
