package SimpactII.DataStructures;

import SimpactII.Agents.Agent;
import java.io.Serializable;

/**
 *
 * @author Lucio Tolentin
 * 
 * Basic data structure to keep track of all relationships that are formed.
 * 
 */
public class Relationship implements Serializable{
    
    private Agent agent1;
    private Agent agent2;
    private double agent1AgeAtFormation;
    private double agent2AgeAtFormation;
    private double start;
    private double end;
    
    public Relationship(Agent a1, Agent a2, double s, double e){
        agent1 = a1;
        agent2 = a2;
        agent1AgeAtFormation = a1.getAge();
        agent2AgeAtFormation = a2.getAge();
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
    public double getAgent1AgeAtFormation() {
        return agent1AgeAtFormation;
    }

    /**
     * @return the agent2Age
     */
    public double getAgent2AgeAtFormation() {
        return agent2AgeAtFormation;
    }
    
    
}
