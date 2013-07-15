/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SleepOp.Agents;

import SimpactII.Agents.Agent;
import SimpactII.Agents.LocalAgent;
import SimpactII.Agents.TriAgeAgent;
import SimpactII.SimpactII;
import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author Lucio
 */
public class TriSleepingAgent extends TriAgeAgent implements SleepingAgentInterface{
    
    private boolean sleeping = false;
    public int warmUp;
    
    public TriSleepingAgent(SimpactII state, HashMap<String,Object> attributes){  
        super(state,attributes);
        
        warmUp = attributes.containsKey("warmUp")? (int) attributes.get("warmUp"): 5;
    }
    
    /*
     * Overwite isLooking() so that agent does not attempt to form new relationships
     * when asleep.
     */
    public boolean isLooking(){
        return !sleeping && super.isLooking();
    }

    /*
     * Overwrite the isSeeking method so that agent appears "awake" when asked 
     * to begin a relationship with Agent other.  
     */    
    public boolean isSeeking(Agent other){
        boolean sleepTemp = sleeping;
        sleeping = false;
        boolean superIsSeeking = super.isSeeking(other);
        sleeping = sleepTemp;
        return superIsSeeking;
    }
    
    /*
     * Overwrite replacement so that agent is 15 and is awake for a few time 
     * steps before falling asleep.
     */
    public Agent replace(SimpactII s){
        //make and agent to set the age
        Agent agent = super.replace(s);
        agent.age = 15;
        
        //cast to sleeping agent to set sleep status
        final SleepingAgentInterface sleepingAgent = (SleepingAgentInterface) agent;
        s.schedule.scheduleOnceIn(warmUp, new Steppable() {
            public void step(SimState ss) { sleepingAgent.setSleeping(true); }
        });
        return (Agent) sleepingAgent;        
    }
    
    public boolean getSleepStatus(){ return sleeping; }

    @Override
    public boolean getSleeping() {
        return sleeping;
    }

    @Override
    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }
    
}
