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
        //DEBUG CODE: rng is getting ArrayIndexOutOfBounds? Could it not get a lock?
        double probability=0.0;
        try{            
            synchronized(state){
//                System.err.print("Lock onto rng " + this.hashCode() + "\t\t");
                probability = state.random.nextDouble();
//                System.err.println(this.hashCode() + " --> release lock " );
            }                    
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println(this.hashCode() + " COULDN'T GET LOCK " );
            System.exit(-1);
        }
//        double probability;
//        //synchronized(rng){ probability = rng.nextDouble(); }
//        synchronized(state.random){ probability = state.random.nextDouble(); }
        
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
