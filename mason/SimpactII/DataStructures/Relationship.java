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
    private double agent1Age;
    private double agent2Age;
    private double start;
    private double end;
    
    public Relationship(Agent a1, Agent a2, double s, double e){
        agent1 = a1;
        agent2 = a2;
        agent1Age = a1.getAge();
        agent2Age = a2.getAge();
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

    /**
     * @return the agent1Age
     */
    public double getAgent1Age() {
        return agent1Age;
    }

    /**
     * @return the agent2Age
     */
    public double getAgent2Age() {
        return agent2Age;
    }
    
    
}
