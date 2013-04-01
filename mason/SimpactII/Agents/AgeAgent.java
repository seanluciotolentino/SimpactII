package SimpactII.Agents;

import SimpactII.SimpactII;
import SimpactII.With.WithAgeMixing;
import SimpactII.With.WithPTRAgents;

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
    
    private double band;
    private double offset;
    
    public AgeAgent(SimpactII state, double band, double offset){
        super(state);
        this.band = band;
        this.offset = offset;
    }
    public AgeAgent(SimpactII state, double band, double offset, int DefaultAge){
        super(state);
        this.band = band;
        this.offset = offset;
        this.age = DefaultAge;
    }
    
    public boolean isDesirable(Agent other){
        boolean ageIsRight;
        if ( isMale() )
            ageIsRight = (getAge() - other.getAge() ) < offset + band && (getAge() - other.getAge() ) > offset - band ; 
        else
            ageIsRight = (other.getAge() - getAge() ) < offset + band && (other.getAge() - getAge() ) > offset - band ; 
        
        return ageIsRight && other.isLookingFor(this);
    }
    public boolean isLookingFor(Agent other){
        return getPartners() < getDNP() && (isMale() ^ other.isMale());
    }
    
    public Agent replace(SimpactII state){
        return new AgeAgent(state,band,offset,15);
    }
}
