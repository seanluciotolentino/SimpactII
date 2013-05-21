package SimpactII.Graphs;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import java.awt.Color;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * Class to generate the demographics box plot.  It does this by iterating from 
 * the beginning of the simulation (time step 0) to the current time step.  At 
 * each time step it creates a bar chart with "numBoxes" boxes of "boxSize" in 
 * years. Each is added as a data point to the data set.   
 * 
 */
public class Demographics extends JFrame{
    
    private int numBoxes = 9;
    private int boxSize = 10;
    private int timeGranularity = 52; //4 = 1 month, 52 = 1 year, etc...
    
    public Demographics(int numBoxes, int boxSize, int timeGranularity, SimpactII state){
        this.numBoxes = numBoxes;
        this.boxSize = boxSize;
        this.timeGranularity = timeGranularity;
        new Demographics(state);
    }
    
    public Demographics(SimpactII state){
        super("Demographics");
        
        //create data
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        int[] demographic;
        int numAgents = state.myAgents.size();
        double now = Math.min(state.numberOfYears*52, state.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        for (int t = timeGranularity; t < now; t += timeGranularity) { //Go through each time step (skipping the first)
            demographic = new int[numBoxes]; //create an int[] with the number of slots we want
            
            //go through agents...
            for (int i = 0; i < numAgents; i++) { 
                Agent agent = (Agent) state.myAgents.get(i);
                if (agent.getTimeOfAddition() >= t || agent.timeOfRemoval < t){  continue; } //skip if the agent wasn't born yet or has been removed

                double age = agent.getAge()*52; //convert age to weeks
                double ageAtT = age - now + t;        
                ageAtT/= 52; //convert back to years
                int level = (int) Math.min(numBoxes-1, Math.floor( ageAtT / boxSize)); 
                demographic[ level ] += 1; //...and add them to their delineations level
            }
            
            //add the delineations to the data
            for (int j = 0; j < numBoxes; j++) { 
                data.addValue(demographic[j], j*boxSize + " - " + (j+1)*boxSize, t + "");
            }
        }
        
        //create the chart
        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Demographics", 
                "Time (weeks)",
                "Population (number of persons)",
                data,
                PlotOrientation.VERTICAL,
                true,   //legend
                false,   //tooltips
                false); //urls
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.getDomainAxis().setCategoryMargin(0.0);
                
        //change the renderering of the bars
        BarRenderer br = (BarRenderer)plot.getRenderer();
        br.setShadowVisible(false);
        br.setBarPainter(new StandardBarPainter());
        
        //br.setItemMargin(0.0);
        //plot.setRenderer(br);
        
        
        //silly GUI stuff       
        ChartPanel chartPanel = new ChartPanel(chart);
        
        setContentPane(chartPanel);
        pack();
        setVisible(true);
    } //end constructor
    
}
