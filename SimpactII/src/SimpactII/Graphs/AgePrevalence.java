/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Graphs;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import java.awt.Color;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author visiting_researcher
 */
public class AgePrevalence extends JFrame{
    
    public AgePrevalence(SimpactII state){
        super("Age-specific HIV prevalence");
        
        //make the data
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        int[][] prevalence = new int[2][7];
        double[][] population = new double[2][7];
        int numAgents = state.myAgents.size(); 
        double t, now = Math.min(state.numberOfYears*52, state.schedule.getTime()); 
        for (int i = 0; i < numAgents; i++) { 
            Agent agent = (Agent) state.myAgents.get(i);
            int age = (int) Math.floor(agent.age/5)-3 ;  
            int gender = agent.isMale()? 0:1;
            if(age<0 || age>6 || agent.timeOfRemoval < now ){continue;} //skip childern
            
            //add to population counts
            population[gender][age]++;
            
            //add to prevalence counts (if applicable)
            if(agent.weeksInfected>0){ prevalence[gender][age]++;}
        }
            
        //add it all to "data"
        double pop = 0;
        double prev = 0;
        for(int age = 0; age < 7; age++){
            //after everyone for this time step, add to the data
//            System.out.println("====age " + ((age+3)*5) + "======");
//            System.out.println("male: " + prevalence[0][age] / population[0][age]);
//            System.out.println("female: " + prevalence[1][age] / population[1][age]);
            pop+=population[0][age]+population[1][age];
            prev+=prevalence[0][age]+prevalence[1][age];
            data.addValue( prevalence[1][age] / population[1][age], "female", ((age+3)*5) + "-" + (((age+3)*5)+4));
            data.addValue( prevalence[0][age] / population[0][age], "male", ((age+3)*5) + "-" + (((age+3)*5)+4));            
        }
        System.out.println("PREVALENCE = " + prev / pop);
        
        //create the chart
                JFreeChart chart = ChartFactory.createBarChart(
                "Age-specific HIV prevalence", 
                "Age",
                "Prevalence",
                data,
                PlotOrientation.VERTICAL,
                true,   //legend
                false,   //tooltips
                false); //urls
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        final NumberAxis rangeAxis = new NumberAxis("HIV Prevalence (%)");
        rangeAxis.setRange(0.0,1.0);
        plot.setRangeAxis(rangeAxis);
        //plot.getDomainAxis().setCategoryMargin(0.0);
                
        //change the renderering of the bars
        BarRenderer br = (BarRenderer)plot.getRenderer();
        br.setItemMargin(0.0);
        br.setShadowVisible(false);
        br.setBarPainter(new StandardBarPainter());
        
        
        //silly GUI stuff       
        ChartPanel chartPanel = new ChartPanel(chart);        
        setContentPane(chartPanel);
        pack();
        setVisible(true);
    }
    
}
