/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;
import sim.util.Distributions.Distribution;
import sim.util.Distributions.UniformDistribution;

/**
 *
 * @author Lucio Tolentino
 * 
 * This is a sex worker agent which unlike a basic agent, has a high desired number
 * of partners and pulls relationship duration from a different distribution (such 
 * that their relationships are shorter). 
 * 
 */
public class SexWorkerAgent extends Agent{
    
    Distribution swRelationshipDistribution = new UniformDistribution(1.0, 2.0);
    double MIN_AGE = 15;
    double MAX_AGE = 30;
    
    public SexWorkerAgent(SimpactII state){
        super(state);
        this.DNP = 16;      //let's say this is the maximum she can have in a week
        this.age = (state.random.nextDouble()*(MAX_AGE - MIN_AGE)) + MIN_AGE; //random age between MIN and MAX
        this.male = false; //all sex-workers are female (in this model)
    }
    public Agent replace(SimpactII state){
        Agent a = new Agent(state); //create a new basic agent to replace the sexworker
        a.age = MAX_AGE;
        a.male = false;
        return a;
    }
    public String toString(){
        return "SexWorker" + this.hashCode();
    }
    public double informRelationship(Agent other){
        super.informRelationship(other);
        return swRelationshipDistribution.nextValue();        
    }
    
    
}
