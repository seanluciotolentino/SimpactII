/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;

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
    
    public SexWorkerAgent(SimpactII state){
        super(state);
        this.DNP = 16;      //let's say this is the maximum she can have in a week
        this.age = state.random.nextInt(15) + 15; //random age between 15 and 30
        this.male = false; //all sex-workers are female (in this model)
    }
    
    public String toString(){
        return "SexWorker" + this.hashCode();
    }
    
    
}
