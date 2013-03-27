/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.SyphilisAgent;
import SimpactII.InfectionOperators.InfectionOperator;
import SimpactII.SimpactII;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class WithSyphilis extends SimpactII{
    
    public double syphilisInfectivity = 0.3;
    public int intialNumberSyphilisInfected = 5;
    
    public WithSyphilis(){
        super();
    }
    
    public void addAgents(){
        for (int i = 0; i<population; i++)
            new SyphilisAgent(this);
    }
    
    public SyphilisAgent addAgent(){
        return new SyphilisAgent(this);
    }
    
    public void addInfectionOperator() {
        schedule.scheduleRepeating(new InfectionOperator(this) {
            public void infectionStep(Agent agent, SimpactII state) {
                super.infectionStep(agent, state); //infect HIV AND also Syphilis
                SyphilisAgent sAgent = (SyphilisAgent) agent;
                if (sAgent.getSyphilisWeeksInfected() >= 1) {
                    sAgent.syphilisWeeksInfected++;

                    Bag partners = state.network.getEdges(agent, new Bag());
                    for (int j = 0; j < partners.size(); j++) {
                        Edge relationship = (Edge) partners.get(j);
                        SyphilisAgent partner = (SyphilisAgent) relationship.getOtherNode(agent);

                        if (partner.getSyphilisWeeksInfected() <= 0
                                && state.random.nextDouble() < syphilisInfectivity) {
                            partner.setInfector(agent);
                            partner.syphilisWeeksInfected = 1;
                        }
                    } //partners for loop
                } //if agent infected
            }
            @Override
            public double infectivity(Agent a){ 
                return 0.01;
            }

            public void performInitialInfections(SimpactII state) { 
                super.performInitialInfections(state); //perform initial HIV infections AND syphilis infections
                for (int i = 0; i < intialNumberSyphilisInfected; i++) {
                    SyphilisAgent agent = (SyphilisAgent) state.myAgents.get(state.random.nextInt(state.population));
                    agent.syphilisWeeksInfected = 1;
                }
            }
        });
    }
    
    public static void main (String[] args){
        WithSyphilis ws = new WithSyphilis();
        ws.run();
        ws.prevalence();
    }
    
    
}
