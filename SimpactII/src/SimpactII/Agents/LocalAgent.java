package SimpactII.Agents;

import SimpactII.SimpactII;
import java.util.HashMap;
import sim.util.Bag;
import sim.util.Double2D;

/**
 *
 * @author Lucio Tolentino
 * 
 * This kind of agent forms relationships with other agents that are only within
 * a certain radius of him/herself. 
 * 
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
    /*
     * Returns partners that are within <b>radius</b> of the agent. <b>Radius</b>
     * is a settable attribute of the agent.
     */
    public Bag possiblePartners(SimpactII state){
        Double2D myLocation = (Double2D) this.attributes.get("location");
        return state.world.getObjectsWithinDistance(myLocation, radius); //this may include other objects within the radius -- don't think this is a problem but it could be...?
    }
    /*
     * Returns whether <b>other</b> agent is within the radius of this agent. 
     * Additionally, the default isLooking() and heterosexual preference must be
     * met. 
     */
    public boolean isSeeking(Agent other){
        Double2D myLocation = (Double2D) this.attributes.get("location");
        Double2D otherLocation = (Double2D) other.attributes.get("location");
        return (myLocation.distance(otherLocation) < radius) && (isMale() ^ other.isMale()) && isLooking();
    }
    /*
     * Returns the radius attribute for this agent.
     */
    public double getRadius(){ return radius; }
    
}
