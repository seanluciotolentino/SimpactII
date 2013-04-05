/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;
import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * Agent which only forms a certain number of relationships a year. After which
 * the agent "is[not]Looking". At the end of the year a 'clearing agent' resets
 * the number of partners this year for the individuals (see WithPTRAgents).
 * 
 * Note that this requires an additional operator / anonymous agent to be added
 * to the schedule to reset agent's number of partners per year (see WithPRRAgents).
 * 
 */
public class PTRAgent extends Agent{
    
    public int partnersPerYear;
    public int partnersThisYear = 0;
    
    public PTRAgent(SimpactII state, HashMap<String,Object> attributes){  
        super(state,attributes);
        
        //set class fields
        Integer ppy = (Integer) attributes.get("partnersPerYear");
        if( ppy == null){ //use default if not supplied
            attributes.put("partnersPerYear",2);
            this.partnersPerYear = 2;
        }else{
            this.partnersPerYear = (int) ppy;
        }
        
        //schedule helper to come alive and reset partner every year
        final PTRAgent myAgent = this;
        state.schedule.scheduleRepeating(new Steppable() {
            public void step(SimState s) { myAgent.resetPartners(); }
            },52.0); //he only wakes up every 52 steps
    }
    //how are relationships formed?
    public boolean isLooking(){
        return partnersThisYear < partnersPerYear;
    }
    public double informRelationship(Agent other){
        //increment my partners this year
        partnersThisYear++;
        return super.informRelationship(other);
    }
    public Agent replace(SimpactII state){
        return new PTRAgent(state,attributes); //replace with something similar
    }
    public String toString(){ return "PTRAgent" + this.hashCode(); }
    
    //extra methods for PTR agents
    public void resetPartners(){
        partnersThisYear = 0;
    }    
    public int getPartnersThisYear(){
        return partnersThisYear;
    }
    
}
