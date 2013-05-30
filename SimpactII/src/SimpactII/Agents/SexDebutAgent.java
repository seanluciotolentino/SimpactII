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
public class SexDebutAgent extends BandAgeAgent{
    
    private double debutAge = 15;
        
    public SexDebutAgent(SimpactII state, HashMap<String,Object> attributes){
        super(state,attributes);      
        
        try{
            if ( attributes.get("debutAge") != null ){ debutAge = (double) attributes.get("debutAge");}
        }catch(Exception e){
            System.err.println("debutAge provided not castable to double. Please provide double (not ints or Strings).\n" + e);
            System.exit(-2);
        }
    }
    
    public boolean isLooking(){
        return (age > debutAge) && super.isLooking();
    }
    
    public Agent replace(SimpactII state){
        return new SexDebutAgent(state,attributes);
    }

    /**
     * @return the debutAge
     */
    public double getDebutAge() {
        return debutAge;
    }
    
}
