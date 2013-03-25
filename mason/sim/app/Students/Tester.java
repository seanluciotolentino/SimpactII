/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.Students;

import ec.util.MersenneTwisterFast;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import sim.util.distribution.Distributions;
/**
 *
 * @author visiting_researcher
 */
public class Tester {
    
    //private Function f = sim.util.distribution.Distributions.nextPowLaw;
    
    public static void main(String[] args){
        /*MersenneTwisterFast mtf = new MersenneTwisterFast();
        double cut = 1;
        double alpha = 2;
        for(int i = 0; i < 20; i++){
            double degree = sim.util.distribution.Distributions.nextPowLaw(alpha, cut, mtf);
            System.out.println(degree + "");
        }*/
        
        /*DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("A", new Integer(75));
        pieDataset.setValue("B", new Integer(10));
        pieDataset.setValue("C", new Integer(10));
        pieDataset.setValue("D", new Integer(5));
        JFreeChart chart = ChartFactory.createPieChart
            ("CSC408 Mark Distribution", // Title
            pieDataset, // Dataset
            true, // Show legend
            true, // Use tooltips
            false // Configure chart to generate URLs?
            );
        JFrame frame = new JFrame();*/
        
        PieChart demo = new PieChart("Comparison", "Which operating system are you using?");
        demo.pack();
        demo.setVisible(true);
    }
}
