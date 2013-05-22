package SimpactII.Agents;

import SimpactII.SimpactII;
import SimpactII.With.WithAgeMixing;
import SimpactII.With.WithPTRAgents;
import ec.util.MersenneTwisterFast;
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
public class ConeAgeAgent extends Agent{
    
    //default values of an AgeAgent
    private double probabilityMultiplier;
    private double preferredAgeDifference;
    protected MersenneTwisterFast rng;
    private double preferredAgeDifferenceGrowth;
    private double adDispersion;
    
        
    public ConeAgeAgent(SimpactII state, HashMap<String,Object> attributes){  
        super(state,attributes);
        rng = state.random;
        preferredAgeDifference = this.isMale()? 3: -3;
        probabilityMultiplier = -0.1;
        preferredAgeDifferenceGrowth = 2;
        adDispersion = 0.01;
        
        //set defaults if not yet set
        try{
            if ( attributes.get("probabilityMultiplier") != null )
                probabilityMultiplier = (double) attributes.get("probMult");
            if ( attributes.get("preferredAgeDifference") != null )
                preferredAgeDifference =  (double) attributes.get("prefAD");
                preferredAgeDifference = this.isMale()? preferredAgeDifference: -preferredAgeDifference;
            if ( attributes.get("preferredAgeDifferenceGrowth ") != null )
                preferredAgeDifferenceGrowth = (double) attributes.get("adGrowth");
            if ( attributes.get("adDispersion") != null )
                adDispersion = (double) attributes.get("adDispersion");
        }catch(Exception e){
            System.err.println("Attribute provided not castable to double. Please provide double (not ints or Strings).\n" + e);
            System.exit(-2);
        }
    }
    public boolean isLookingFor(Agent other){        
        return ageIsRight(other) && other.isSeeking(this);
    }
    public boolean isSeeking(Agent other){
        return getPartners()<getDNP() && (isMale() ^ other.isMale()) && ageIsRight(other);
    }
    
    public Agent replace(SimpactII state){
        Agent a = new ConeAgeAgent(state,attributes );
        a.age = 15;
        return a;
    }
    public boolean ageIsRight(Agent other){
        double probability = rng.nextDouble();
        double ageDifference = this.age - other.age;
        double meanAge = ((this.age + other.age)/2)-15;
        
        //return  probability < Math.exp(probMult * Math.abs(ageDifference - (prefAD*meanAge*adGrowth) ) );        
        return probability < Math.exp(probabilityMultiplier * (Math.abs(ageDifference - (preferredAgeDifference*meanAge*preferredAgeDifferenceGrowth) ) /
                                            (preferredAgeDifference*meanAge*adDispersion)) );
    }
}
