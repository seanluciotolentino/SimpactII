/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;
import java.util.HashMap;

/**
 *
 * @author visiting_researcher
 */
public class BiAgent extends Agent{
    
    public BiAgent(SimpactII state, HashMap<String,Object> attributes){  
        super(state,attributes);
        this.male = true;
    }
    
    public boolean isLookingFor(Agent other){
        return other.isSeeking(this) ;
    }
    public boolean isSeeking(Agent other){
        return isLooking();
    }
    public String toString(){
        return "MSM" + this.hashCode();
    }
    
}
