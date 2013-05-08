/* 
 * Copyright 2011 by Mark Coletti, Keith Sullivan, Sean Luke, and
 * George Mason University Mason University Licensed under the Academic
 * Free License version 3.0
 *
 * See the file "LICENSE" for more information
 *
 * $Id: Agent.java 678 2012-06-24 21:01:14Z mcoletti $
*/
package SimpactII.GeoTests;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.geo.GeomVectorField;
import sim.util.Bag;
import sim.util.geo.AttributeValue;
import sim.util.geo.MasonGeometry;

/**
 *  Our simple agent for the ColorWorld GeoMASON example.  The agents move in one of the eight cardinal directions 
 *  until they hit the boundary of Fairfax County.  Then, they choose a random direction, and repeat.   
 *
 */

public class Agent implements Steppable {

    private static final long serialVersionUID = -5318720825474063385L;
    // possible directions of movement
    final int N  = 0; 
    final int NW = 1; 
    final int W  = 2;
    final int SW = 3;
    final int S  = 4;
    final int SE = 5; 
    final int E  = 6; 
    final int NE = 7;
    
    int direction;// Current direction the agent is moving
    Point location = null;// agent's position
    double moveRate = 1.0;// How much to move the agent by in each step()
    
    //is agent away from home?
    public final Point home;
    public boolean atHome = true;
     
    public Agent(Point loc) {
        home = (Point) loc.clone(); //must close other the home moves with the location!
        location = loc;
    }
            
    public void setLocation(Point p) { 
        double newX = p.getX();
        double newY = p.getY();
        
        double oldX = location.getX();
        double oldY = location.getY();
        
        //System.out.println("X: " + (newX - oldX) + " Y: " + (newY - oldY));
        
        AffineTransformation translate = 
                AffineTransformation.translationInstance(newX - oldX, newY - oldY);
        location.apply(translate);
        
//        location = p;
    }

    public Geometry getGeometry() { return location; }
    
    public boolean getAtHome(){ return atHome;}
    
    public void step(SimState s) {
        GeoMasonTest state = (GeoMasonTest) s;
        
        //if away from home, move back
        if(!atHome){ 
            //System.out.print("Agent " + this.hashCode() + " is moving home: ");
            
            setLocation(home);
            atHome = true;
            return;
        }
        //else flip a coin whether to "migrate"
        if(state.random.nextDouble() < 0)
            return;
        
        
        //System.out.println("Agent " + this.hashCode() + " is moving away: ");
        //attempt to get them to move just 50 km west
        //System.out.println("    -> Location before " + location.getX() + " " + location.getY() );
        //AffineTransformation translate = AffineTransformation.translationInstance(-1,0);
        //location.apply(translate);
        //System.out.println("    -> Location after " + location.getX() + " " + location.getY() );
        
        //AffineTransformation.translationInstance(location., moveRate)
        
        
                
        //have agent move to the next closest district
        atHome = false;
        //System.out.print("Agent " + this.hashCode() + " wants to move ");
        double travelDistance = 1; //1 degree is approx 111km ~ 69 miles?
        Bag nearestDistricts = state.magisterialDistricts.getObjectsWithinDistance(location,travelDistance);
        if (nearestDistricts.isEmpty()) { 
            //System.out.println("but can't -- no nearby districts");
            return; //you don't get to migrate
        } 
        
        //else
        MasonGeometry nearestDistrict = (MasonGeometry) nearestDistricts.get(state.random.nextInt(nearestDistricts.size()));
        setLocation(nearestDistrict.geometry.getInteriorPoint() );
        
//        System.out.print("AND IS! -- to " + ((AttributeValue) nearestDistrict.getAttribute("NAME_2")).getValue()
//                + ", " + ((AttributeValue) nearestDistrict.getAttribute("NAME_1")).getValue()
//                + ", " + ((AttributeValue) nearestDistrict.getAttribute("NAME_0")).getValue());
//        System.out.println(". Location was " + home + " and now is " + location);
        
        
        
        
        
        
        
        
        
        
        
//        if(!atHome){//go back
//            System.out.println("Agent " + this.hashCode() + " Moving home " + state.schedule.getTime());
//            setLocation(home);
//            atHome = true;
//            return;
//        }
//        if(state.random.nextFloat() > 0.25) //only do this for 25% of the population
//            return;
//        // try to move the agent, keeping the agent inside its political region
//        System.out.println("Agent " + this.hashCode() + " going away at time " + state.schedule.getTime() );    
//    	GeoMasonTest cState = (GeoMasonTest)state; 
//        GeomVectorField world = cState.magisterialDistricts;
//        Coordinate coord = (Coordinate) location.getCoordinate().clone();
//        AffineTransformation translate = null;
//        atHome = false;
//        switch (direction)
//            {
//            case N : // move up
//                translate = AffineTransformation.translationInstance(0.0, moveRate);
//                coord.y += moveRate;
//                break;
//            case S : // move down
//                translate = AffineTransformation.translationInstance(0.0, -moveRate);
//                coord.y -= moveRate;
//                break;
//            case E : // move right
//                translate = AffineTransformation.translationInstance(moveRate, 0.0);
//                coord.x += moveRate;
//                break;
//            case W : // move left
//                translate = AffineTransformation.translationInstance(-moveRate, 0.0);
//                coord.x -= moveRate;
//                break;
//            case NW : // move upper left
//                translate = AffineTransformation.translationInstance(-moveRate,moveRate);
//                coord.x -= moveRate;
//                coord.y += moveRate; 
//                break;
//            case NE : // move upper right
//                translate = AffineTransformation.translationInstance( moveRate, moveRate );
//                coord.x += moveRate;
//                coord.y += moveRate;
//                break;
//            case SW : // move lower left
//                translate = AffineTransformation.translationInstance(-moveRate, -moveRate);
//                coord.x -= moveRate;
//                coord.y -= moveRate;
//                break;
//            case SE : // move lower right
//                translate = AffineTransformation.translationInstance( moveRate, -moveRate);
//                coord.x += moveRate;
//                coord.y -= moveRate;
//                break;
//            }
//
//        	//cState.county.updateTree(location, translate); 
//        	location.apply(translate);
//        
//            direction = state.random.nextInt(8);
    }
}
