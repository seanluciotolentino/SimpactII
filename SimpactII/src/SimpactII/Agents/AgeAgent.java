package SimpactII.Agents;

import SimpactII.SimpactII;
import SimpactII.With.WithAgeMixing;
import SimpactII.With.WithPTRAgents;
import java.util.HashMap;

/**
 *
 * @author Lucio Tolentino
 * 
 * Initially used with "WithAgeMixing" model. This agent takes a band and an offset
 * to determine which partners to choose.  The 'offset' variable determines the 
 * preferred age difference (the offset set from 0 age different). The 'band' 
 * variable determines the flexibility around the preferred age difference. 
 * 
 * To do this we overridded the methods isDesirable and isLookingFor. isLooking
 * stays the same as the default.
 * 
 */
public class AgeAgent extends Agent{
    
    //default values of an AgeAgent
    private double band = 5;
    private double offset = 5;
        
    public AgeAgent(SimpactII state, HashMap<String,Object> attributes){  
        super(state,attributes);
        
        //set defaults if not yet set
        try{
            if ( attributes.get("band") != null )
                band = (double) attributes.get("band");
            if ( attributes.get("offset") != null )
                offset =  (double) attributes.get("offset");
        }catch(Exception e){
            System.err.println("Band and / or offset provided not castable to double. Please provide double (not ints or Strings).\n" + e);
            System.exit(-2);
        }
    }
    public boolean isLookingFor(Agent other){
        boolean ageIsRight;
        if ( isMale() )
            ageIsRight = (getAge() - other.getAge() ) < offset + band && (getAge() - other.getAge() ) > offset - band ; 
        else
            ageIsRight = (other.getAge() - getAge() ) < offset + band && (other.getAge() - getAge() ) > offset - band ; 
        
        return ageIsRight && other.isSeeking(this);
    }
    public boolean isSeeking(Agent other){
        return getPartners() < getDNP() && (isMale() ^ other.isMale());
    }
    
    public Agent replace(SimpactII state){
        return new AgeAgent(state,attributes );
    }
}
