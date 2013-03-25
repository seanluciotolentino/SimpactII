/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII;

import SimpactII.Agents.Agent;

/**
 *
 * @author visiting_researcher
 */
public class Relationship {
    
    private Agent agent1;
    private Agent agent2;
    private double start;
    private double end;
    
    public Relationship(Agent a1, Agent a2, double s, double e){
        agent1 = a1;
        agent2 = a2;
        start = s;
        end = e;
    }

    /**
     * @return the agent1
     */
    public Agent getAgent1() {
        return agent1;
    }

    /**
     * @return the agent2
     */
    public Agent getAgent2() {
        return agent2;
    }

    /**
     * @return the start
     */
    public double getStart() {
        return start;
    }

    /**
     * @return the end
     */
    public double getEnd() {
        return end;
    }
    
    
}
