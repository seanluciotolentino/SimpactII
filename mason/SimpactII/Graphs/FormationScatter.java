/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Graphs;

import SimpactII.Agents.Agent;
import SimpactII.Relationship;
import SimpactII.SimpactII;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import sim.field.network.Edge;
import sim.util.Bag;
import sim.util.Double2D;

/**
 *
 * @author visiting_researcher
 */
public class FormationScatter extends JFrame{
    
    public FormationScatter(SimpactII state){
        super("Relationship Formation Scatter");
        
        //create XYseries
        XYSeries data = new XYSeries("Formation Scatter");        
        int numRelations = state.relations.size();
        for(int j = 0 ; j < numRelations; j++){
            Relationship r = (Relationship) state.relations.get(j);
            data.add(r.getAgent1().getAge() , r.getAgent2().getAge() ); 
        }
        
        //add the series to the collection?
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data);
        
        //chart the series
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Relationship formation scatter", 
                "Male Age", 
                "Female Age", 
                dataset, 
                PlotOrientation.HORIZONTAL, 
                false,   //legend
                false,  //tooltips
                false); //urls
        ChartPanel chartPanel = new ChartPanel(chart);
        super.setContentPane(chartPanel);
        super.pack();
        super.setVisible(true);
    }
    
}
