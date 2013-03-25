/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII;

import SimpactII.Agents.Agent;
import SimpactII.With.WithMSM;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JFrame;
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
    ContinuousPortrayal2D worldPortrayal = new ContinuousPortrayal2D();
    NetworkPortrayal2D networkPortrayal = new NetworkPortrayal2D();
    
    public static void main(String[] args){
        GUI vid = new GUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }
    
    public GUI() {
        super(new SimpactII(System.currentTimeMillis()));        
        //super(new WithMSM());
    }
    
    public GUI(SimState state){
        super(state);        
    }
    
    public void start(){
        super.start();
        setupPortrayals();
    }
    
    public void load(SimState state){
        super.load(state);
        setupPortrayals();
    }

    public void setupPortrayals() {
        SimpactII simpactII = (SimpactII) state; //How is state within this scope?
        
        //tell the portrayals what to portray and how to portray them
        worldPortrayal.setField(simpactII.world);
        worldPortrayal.setPortrayalForAll(
                new MovablePortrayal2D(
                    new CircledPortrayal2D(
                        new LabelledPortrayal2D(
                            new OvalPortrayal2D()
                                { //anonymous subclassing: change the draw function so color is always agitationShade
                                public void draw(Object o, Graphics2D graphics, DrawInfo2D info){
                                    Agent ba = (Agent) o;
                                    if ( ba.isMale() )
                                        paint = Color.blue;
                                    else 
                                        paint = Color.red;
                                
                                    if ( ba.weeksInfected > 0 ) 
                                        paint = Color.GREEN;

                                    super.draw(o,graphics,info);
                                    }
                                },//color of the node
                            5.0,null, Color.black, true), //label of node
                0,5.0, Color.green,true))); //circle around node when selected
        
        //tell network portrayal what to portray
        networkPortrayal.setField(new SpatialNetwork2D(simpactII.world, simpactII.network));
        networkPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
        
        
        //reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);
        
        //redraw the display
        display.repaint();
        
    }
    
    public void init(Controller c){
        super.init(c);

        // make the displayer
        display = new Display2D(600,600,this);
        // turn off clipping
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Sexual Network");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach( worldPortrayal, "The World" );
        display.attach(networkPortrayal,"Sexual Network");

    }
    
    public Object getSimulationInspectedObject() { return state; }
    
    public void quit(){
        super.quit();
        if(displayFrame!=null) displayFrame.dispose();
        
        displayFrame = null;
        display = null;
                
    }
    
}
