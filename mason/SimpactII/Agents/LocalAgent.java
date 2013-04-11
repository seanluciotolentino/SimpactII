/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;
import java.util.HashMap;
import sim.util.Bag;
import sim.util.Double2D;

/**
 *
 * @author visiting_researcher
 */
public class LocalAgent extends Agent{
    
    private double radius = 5; //if this is too big 
    
    public LocalAgent(SimpactII state, HashMap attr){
        super(state,attr);
        try{
            if ( attributes.get("radius") != null )
                radius = (double) attributes.get("radius");
        }catch(Exception e){
            System.err.println("radius provided not castable to double. Please provide double (not ints or Strings).\n" + e);
            System.exit(-2);
        }
    }
    
    public Bag possiblePartners(SimpactII state){
        Double2D myLocation = (Double2D) this.attributes.get("location");
        return state.world.getObjectsWithinDistance(myLocation, radius); //this may include other objects within the radius -- don't think this is a problem but it could be...?
    }
    
    public boolean isLookingFor(Agent other){        
        return isSeeking(other) && other.isSeeking(this);
    }
    
    public boolean isSeeking(Agent other){
        Double2D myLocation = (Double2D) this.attributes.get("location");
        Double2D otherLocation = (Double2D) other.attributes.get("location");
        return (myLocation.distance(otherLocation) < radius) && (isMale() ^ other.isMale()) && isLooking();
    }
    
    public double getRadius(){ return radius; }
    
}
