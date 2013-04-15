/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.TimeOperators;

import SimpactII.Agents.Agent;

/**
 *
 * @author visiting_researcher
 */
public class AIDSDeathTimeOperator extends TimeOperator{
    
    double yearsTillDeath = 8;
    
    //just replace removal
    public boolean remove(Agent agent){
        if(agent.weeksInfected>0){
            //prolong life by time on ARV
            Double ARVstart = (Double) agent.attributes.get("ARVstart");
            Double ARVstop = (Double) agent.attributes.get("ARVStop");
            
            double lifeProlong = 0;
            if(ARVstart != null){
                if(ARVstop == null) //individuals is on ARVs
                    return super.remove(agent);
                else
                    lifeProlong = ARVstop - ARVstart;
            }
            
            
            boolean x = agent.weeksInfected > (52*yearsTillDeath) + lifeProlong; //death after eight years
            if (x){
                System.out.println("Agent " + agent.hashCode() + " removed for"
                        + " AIDS death. Weeks infected = " + agent.weeksInfected);
            }
            return x;
        }
        return super.remove(agent);
    }
    
}
