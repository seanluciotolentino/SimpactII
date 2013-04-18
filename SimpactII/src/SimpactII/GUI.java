/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII;

import SimpactII.Agents.*;
import SimpactII.With.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import sim.display.*;
import sim.engine.SimState;
import sim.portrayal.*;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.network.SimpleEdgePortrayal2D;
import sim.portrayal.network.SpatialNetwork2D;
import sim.portrayal.simple.*;

/**
 *
 * @author Lucio Tolentino
 */
public class GUI extends GUIState{
    
    public Display2D display;
    public JFrame displayFrame;
    public ContinuousPortrayal2D worldPortrayal = new ContinuousPortrayal2D();
    public NetworkPortrayal2D networkPortrayal = new NetworkPortrayal2D();
    
    public GUI() {
        this(new SimpactII() );     
    }
    
    public GUI(SimState state){
        super(state);     
        addSimpactGraphs();
        addAddAgentsPopup();
        Console c = new Console(this);
        c.setVisible(true);
    }
    
    public void start(){
        super.start();
        setupPortrayals();
    }
    
    public void load(SimState state){
        super.load(state);
        setupPortrayals();
    }
    
    public void addSimpactGraphs(){    
        //basic stuff
        JFrame frame = new JFrame("Graphing options");
        JPanel panel = new JPanel(new GridLayout(0,1) ); //1 column, as many rows as needed
        frame.setLocation(1050, 200);        
        final SimpactII s = (SimpactII) super.state;
        
        //perhaps there's a better way to do this then copy / pasting?
        
        //formed relations button
        JButton button = new JButton("Age-Mixing");
        button.addActionListener(new ActionListener()
            {            
            public void actionPerformed(ActionEvent e) {
                s.agemixingScatter();
            }
            });
        panel.add(button);
        
        //formed relations button
        button = new JButton("Demographics");
        button.addActionListener(new ActionListener()
            {            
            public void actionPerformed(ActionEvent e) {
                s.demographics();
            }
            });                
        panel.add(button);
        
        //formed relations button
        button = new JButton("Formed Relationships");
        button.addActionListener(new ActionListener()
            {            
            public void actionPerformed(ActionEvent e) {
                s.formedRelations();
            }
            });                
        panel.add(button);
        
        //prevalence and incidence button
        button = new JButton("Prevalence and Incidence");
        button.addActionListener(new ActionListener()
            {            
            public void actionPerformed(ActionEvent e) {
                s.prevalence();
            }
            });                
        panel.add(button);
        
        //finalize
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void addAddAgentsPopup(){
        //basic stuff
        JFrame frame = new JFrame("Add agents box");
        JPanel panel = new JPanel(new GridLayout(2,2) ); //1 column, as many rows as needed
        frame.setLocation(1050, 0);        
        final SimpactII s = (SimpactII) super.state;
        
        //actually add the stuff
        //agents combo box
        Object[] agents = new Object[] {
            Agent.class, 
            BandAgeAgent.class, 
            MSMAgent.class,
            PTRAgent.class, 
            SexWorkerAgent.class,
            LocalAgent.class};
        final JComboBox cb = new JComboBox(agents);
        panel.add(cb);
        
        //population input
        final JTextField tf = new JTextField("1000");
        panel.add(tf);
        
        //add agents button
        JButton b = new JButton("Add Agents");
        b.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                s.addAgents((Class) cb.getSelectedItem(), Integer.parseInt(tf.getText() ));
            }
        });
        panel.add(b);
        
        //add reset button
        b = new JButton("Reset Population");
        b.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                s.resetPopulations();
            }
        });
        panel.add(b);
        
        //finalize
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        
    }

    public void setupPortrayals() {
        SimpactII s = (SimpactII) super.state;
        
        //tell the portrayals what to portray and how to portray them
        worldPortrayal.setField(s.world);
        worldPortrayal.setPortrayalForAll(
                new MovablePortrayal2D(
                    new CircledPortrayal2D(
                        new LabelledPortrayal2D(
                            new OvalPortrayal2D()
                                { //anonymous subclassing: change the draw function so color is always agitationShade
                                public void draw(Object o, Graphics2D graphics, DrawInfo2D info){
                                    Agent agent = (Agent) o;
                                    paint = howDoIDraw(agent);

                                    super.draw(o,graphics,info);
                                    }
                                },//color of the node
                            5.0,null, Color.black, true), //label of node
                0,5.0, Color.green,true))); //circle around node when selected
        
        //tell network portrayal what to portray
        networkPortrayal.setField(new SpatialNetwork2D(s.world, s.network));
        networkPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
        
        
        //reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);
        
        //redraw the display
        display.repaint();
        
    }
    
    public Color howDoIDraw(Agent agent){
        if (agent.weeksInfected > 0) {
            return Color.GREEN;
        }
        
        if (agent.isMale()) {
            return Color.blue;
        } else {
            return Color.red;
        }
    }
    
    public void init(Controller c){
        super.init(c);

        // make the displayer
        display = new Display2D(600,600,this);
        // turn off clipping
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("SimpactII");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach(worldPortrayal, "The Agents" );
        display.attach(networkPortrayal,"Sexual Network");

    }
    
    public Object getSimulationInspectedObject() { return state; }
    
    public void quit(){
        super.quit();
        if(displayFrame!=null) displayFrame.dispose();
        
        displayFrame = null;
        display = null;
                
    }
                    
    public static void main(String[] args){
        GUI vid = new GUI();
        //Console c = new Console(vid);
        //c.setVisible(true);
    }
    
}
