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
public class AgeAgent extends ConeAgeAgent{
    
    public AgeAgent(SimpactII state, HashMap<String,Object> attributes){ 
        super(state,attributes);
    }
    
    public boolean ageIsRight(Agent other){
        double probability = rng.nextDouble();
        double ageDifference = this.age - other.age;
        //double ageDifference = femaleAge - maleAge;
        //double meanAge = ((this.age + other.age)/2);
        
        return  probability < Math.exp(this.probabilityMultiplier
                * Math.abs(ageDifference - (preferredAgeDifference) ) );            //*meanAge*preferredAgeDifferenceGrowth
    }
    
    
}
