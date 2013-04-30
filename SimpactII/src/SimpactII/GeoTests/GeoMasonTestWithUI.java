/* 
 * Copyright 2011 by Mark Coletti, Keith Sullivan, Sean Luke, and
 * George Mason University Mason University Licensed under the Academic
 * Free License version 3.0
 *
 * See the file "LICENSE" for more information
 *
 * $Id: ColorWorldWithUI.java 678 2012-06-24 21:01:14Z mcoletti $
 */
package SimpactII.GeoTests;

import SimpactII.GUI;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.geo.GeomPortrayal;
import sim.portrayal.geo.GeomVectorFieldPortrayal;
import sim.portrayal.simple.CircledPortrayal2D;
import sim.portrayal.simple.LabelledPortrayal2D;
import sim.portrayal.simple.MovablePortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.gui.SimpleColorMap;

/**
 * The display GUI for the ColorWorld GeoMASON example. Much of this file is
 * similar to other MASON GUI code. The only exception is that we use our custom
 * ColorWorldPortrayal for the voting districts to handle the shading.
 *
 */
public class GeoMasonTestWithUI extends GUIState {



    private Display2D display;
    private JFrame displayFrame;
    private GeomVectorFieldPortrayal myDistricts = new GeomVectorFieldPortrayal();
    private GeomVectorFieldPortrayal myAgents = new GeomVectorFieldPortrayal();

    public GeoMasonTestWithUI(SimState s) {
        super(s);
    }
    public GeoMasonTestWithUI() {
        super(new GeoMasonTest() );
    }

    public void init(Controller controller) {
        super.init(controller);
        display = new Display2D(600, 600, this);
        display.attach(myDistricts, "World");
        display.attach(myAgents, "Agents");
        displayFrame = display.createFrame();
        controller.registerFrame(displayFrame);
        displayFrame.setVisible(true);
    }

    @Override
    public void start() {
        super.start();
        setupPortrayals();
    }

    private void setupPortrayals() {
        GeoMasonTest world = (GeoMasonTest) state;
        myDistricts.setField(world.magisterialDistricts);
        myDistricts.setPortrayalForAll(new GeomPortrayal(Color.BLACK, false));
        
        myAgents.setField(world.agents);
        //myAgents.setPortrayalForAll(new GeomPortrayal(Color.RED, 0.03,true));
        myAgents.setPortrayalForAll(new OvalPortrayal2D(Color.RED, 4.0,true));
//        myAgents.setPortrayalForAll(  
//            new MovablePortrayal2D(
//                new CircledPortrayal2D(
//                    new LabelledPortrayal2D(
//                        new OvalPortrayal2D(Color.RED,4.0,true),
//                    5.0,null, Color.black, true), //label of node
//                0,5.0, Color.green,true) //circle around node when selected
//                ) //moveable
//            ); //portrayal for all
        
        
        display.reset();
        display.setBackdrop(Color.WHITE);
        display.repaint();
    }
    
    public static void main(String[] args){
        GeoMasonTestWithUI vid = new GeoMasonTestWithUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }

}
