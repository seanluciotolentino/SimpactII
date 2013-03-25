/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.MSMAgent;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
import static sim.engine.SimState.doLoop;
import sim.util.Double2D;

/**
 *
 * @author visiting_researcher
 */
public class WithMSM extends SimpactII{
    
    public WithMSM(){
        super();
    }
    
    public void addAgents(){
        for (int i = 0; i < population; i++) {
            new MSMAgent(this); //agent class below
            new Agent(this); //agent class below
        }
    }
    
    public void addTimeOperator(){
        schedule.scheduleRepeating(schedule.EPOCH, 1, 
                new TimeOperator()
                {
                    public boolean remove(){ return false;  }
                }                
                );
    }
    

}
