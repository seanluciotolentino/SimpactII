/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;
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
    
    public PTRAgent(SimpactII state, int ppy){
        super(state);
        this.partnersPerYear = ppy;
        
        //schedule helper to come alive and reset partner every year
        final PTRAgent myAgent = this;
        state.schedule.scheduleRepeating(new Steppable() {
            public void step(SimState s) { myAgent.resetPartners(); }
            },52.0); //he only wakes up every 52 steps
    }
    //inherited methods to override
    public boolean isLooking(){
        return partnersThisYear < partnersPerYear;
    }    
    public Agent replace(SimpactII state){
        return new PTRAgent(state,this.partnersPerYear); //replace with something similar
    }
    public double informRelationship(Agent other){
        //increment my partners this year
        partnersThisYear++;
        return super.informRelationship(other);
    }
    public String toString(){ return "PTRAgent" + this.hashCode(); }
    
    //extra methods
    public void resetPartners(){
        partnersThisYear = 0;
    }    
    public int getPartnersThisYear(){
        return partnersThisYear;
    }
    
}
