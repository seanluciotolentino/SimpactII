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
    
    public boolean isDesirable(Agent other){
        return other.isLookingFor(this) && other.isMale();
    }
    public boolean isLookingFor(Agent other){
        return getPartners() < getDNP() && ( other.isMale() );
    }
    public String toString(){
        return "MSM" + this.hashCode();
    }
}
