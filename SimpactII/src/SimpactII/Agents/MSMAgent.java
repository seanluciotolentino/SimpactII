package SimpactII.Agents;

import SimpactII.SimpactII;
import java.util.HashMap;

/**
 *
 * @author Lucio Tolentino
 * 
 * This is an MSM (men who have sex with men) agent. It overrides the "isDesirable"
 * and "isLookingFor" method.  
 * 
 * No changes to time operator or infection operator is required.
 * 
 */
public class MSMAgent extends Agent{
    
    public MSMAgent(SimpactII state, HashMap<String,Object> attributes){  
        super(state,attributes);
        this.male = true;
    }
    
    public boolean isLookingFor(Agent other){
        return other.isSeeking(this) && other.isMale() && !other.equals(this);//(avoid forming relationship with self)
    }
    public boolean isSeeking(Agent other){
        return isLooking() && ( other.isMale() );
    }
    public String toString(){
        return "MSM" + this.hashCode();
    }
}
