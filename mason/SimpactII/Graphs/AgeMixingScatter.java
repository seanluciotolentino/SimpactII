package SimpactII.Graphs;

import SimpactII.DataStructures.Relationship;
import SimpactII.SimpactII;
import java.awt.BasicStroke;
import java.awt.Color;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Lucio Tolentino
 * 
 * Class to generate the age mixing scatter plot. Each point is a relationship
 * in the simulation with the x-value denoting the age of the female, and the 
 * y-value denoting the age of the male. 
 * 
 */
public class AgeMixingScatter extends JFrame{
    
    public AgeMixingScatter(SimpactII state){
        super("Age Mixing Scatter");
        
        //create XYseries
        XYSeries data = new XYSeries("Formation Scatter");        
        int numRelations = state.allRelations.size();
        for(int j = 0 ; j < numRelations; j++){
            Relationship r = (Relationship) state.allRelations.get(j);
            if (r.getAgent1().isMale())
                data.add(r.getAgent2Age(), r.getAgent1Age()); 
            else
                data.add(r.getAgent1Age(), r.getAgent2Age()); 
        }
        
        //add the series to the collection?
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(data);
        
        //chart the series
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Age Mixing Scatter", 
                "Female Age", 
                "Male Age", 
                dataset, 
                PlotOrientation.VERTICAL, 
                false,   //legend
                false,  //tooltips
                false); //urls
        
        //make the plot look pretty:
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.black);
        plot.setDomainGridlinePaint(Color.black);
        
        //change the axis'
        final NumberAxis domainAxis = new NumberAxis("X-Axis");
        domainAxis.setRange(0.0,75.0);
        domainAxis.setTickUnit(new NumberTickUnit(5.0));
        domainAxis.setLabel("Female Age");
        plot.setDomainAxis(domainAxis);
        
        final NumberAxis rangeAxis = new NumberAxis("Y-Axis");
        rangeAxis.setRange(0.0,75.0);
        rangeAxis.setTickUnit(new NumberTickUnit(5.0));
        rangeAxis.setLabel("Male Age");
        plot.setRangeAxis(rangeAxis);
        
        //add "line of equal age mixing"
        XYLineAnnotation line = new XYLineAnnotation(
            0, 0, 70, 70, new BasicStroke(2f), Color.blue);
        plot.addAnnotation(line);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        super.setContentPane(chartPanel);
        super.pack();
        super.setVisible(true);
    }
    
}
