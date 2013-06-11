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
public class TriAgeAgent extends ConeAgeAgent{
    
    public TriAgeAgent(SimpactII state, HashMap<String,Object> attributes){ 
        super(state,attributes);
    }
    
    public boolean ageIsRight(Agent other){
        double probability = rng.nextDouble();
        double ageDifference = this.age - other.age;
        //double ageDifference = femaleAge - maleAge;
        //double meanAge = ((this.age + other.age)/2);
        
        //return probability < Math.exp(meanAgeFactor*meanAge)*Math.exp(this.probabilityMultiplier
        //        * Math.abs(ageDifference - (preferredAgeDifference) ) );            
        return probability < Math.exp(this.probabilityMultiplier
                * Math.abs(ageDifference - (preferredAgeDifference) ) );            
    }
    
    public Agent replace(SimpactII state){
        return new TriAgeAgent(state, attributes);
    }
    
    
}
