/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.SexWorkerAgent;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
import sim.util.Distributions.Distribution;
import sim.util.Distributions.UniformDistribution;

/**
 *
 * @author luciotolentino
 */
public class WithSexWorkers extends SimpactII{
    
    private int numSexWorkers = 10; 
    private Distribution swRelationshipDistribution = new UniformDistribution(1.0, 2.0); //sw = sex work
    
    public WithSexWorkers(){
        super();
        this.timeOperator = new TimeOperator(){ //modify the time operator
            public boolean remove(Agent agent){
                if(agent.getClass() == SexWorkerAgent.class)
                    return agent.getAge() > 30;
                else
                    return super.remove(agent);
            }
        };
    }
    
    public void addAgents(){
        //add basic agents
        addNAgents(population,Agent.class);
        
        //and then add sex worker agents in addition
        addNAgents(numSexWorkers,SexWorkerAgent.class);
    }
    
    public void formRelationship(Agent agent1, Agent agent2){
        double duration;
        if (agent1.getClass() == SexWorkerAgent.class || agent2.getClass() == SexWorkerAgent.class)
            duration = swRelationshipDistribution.nextValue();
        else
            duration = relationshipDurations.nextValue();
        super.formRelationship(agent1, agent2, duration);
    }
    

    
    
}
