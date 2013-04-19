/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;
import java.util.HashMap;
import sim.util.distribution.Distributions;

/**
 *
 * @author visiting_researcher
 */
public class ExtremeAgeAgent extends ConeAgeAgent{
    private double adAge;
    
    public ExtremeAgeAgent(SimpactII s, HashMap hm){
        super(s,hm);
        adAge =  1; //age at which he starts becoming AD friendly
        DNP = 3;
        try{
            if ( attributes.get("adAge") != null && this.isMale() )
                adAge = (double) attributes.get("adAge");
        }catch(Exception e){
            System.err.println("Attribute provided not castable to double. Please provide double (not ints or Strings).\n" + e);
            System.exit(-2);
        }
    }
    
    public boolean ageIsRight(Agent other){
        boolean ad = (Math.abs(this.age - other.age) > 5);
        if (isMale())
            return !(this.age>adAge ^ ad); //male only does AD once he's past the AD age
        else
            return this.age <100;// || super.rng.nextDouble() < 0.1; //females looking for any age

    }
    public double getADAge(){ return adAge; }
    
}
