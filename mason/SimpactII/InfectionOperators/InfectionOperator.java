/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.InfectionOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 */
public class InfectionOperator implements Steppable{
    
    //default class variables about infection
    public double transmissionProbability = 0.01;
    public int initialNumberInfected = 5;
    
    public InfectionOperator(SimpactII s){
        new InfectionOperator( initialNumberInfected , s);
    }
    
    public InfectionOperator(int initialNumberInfected,SimpactII s){
        //perform initial infections
        this.initialNumberInfected = initialNumberInfected;
        for(int i = 0; i < initialNumberInfected; i++){
            Agent agent = (Agent) s.myAgents.get(s.random.nextInt(s.population));
            agent.weeksInfected = 1;
        }
    }
        
    public void step(SimState sim){
        SimpactII state = (SimpactII) sim;
        
        //flip coin for possible infections
        Bag agents = state.network.getAllNodes();
        for(int i = 0 ; i < agents.size(); i++){
            Agent agent = (Agent) agents.get(i);
            
            infectionStep(agent,state);           

        } //for all agents        
    } //end step
    
    private double infectivity(Agent agent) {
        return transmissionProbability;       
    }

    private void infectionStep(Agent agent, SimpactII state) {
        //find those that are actually infected
        if (agent.weeksInfected>= 1){ 
            agent.setWeeksInfected(agent.getWeeksInfected() + 1);

            Bag partners = state.network.getEdges(agent,new Bag());
            for(int j = 0 ; j < partners.size(); j++){
                Edge relationship = (Edge) partners.get(j);
                Agent partner = (Agent) relationship.getOtherNode(agent);

                if (partner.weeksInfected<=0 && state.random.nextDouble() < infectivity(agent) ){
                    partner.setInfector(agent);
                    partner.weeksInfected = 1;
                }
            } //partners for loop
        } //if agent infected
    }
    
}
