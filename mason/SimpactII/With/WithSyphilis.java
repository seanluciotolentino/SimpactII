package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.SyphilisAgent;
import SimpactII.InfectionOperators.InfectionOperator;
import SimpactII.SimpactII;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * This provides an example of how to use SimpactII with co-infection with another 
 * STI like syphilis. We make use of the default code with some extra provisions
 * to also allow the transmission of syphilis. (1) First, we create a new Agent
 * (called SyphilisAgent) which inherits from the default agent. The only difference
 * from the original is that it has an additional class variable "syphilisWeeksInfected".
 * 
 * (2) Second, we create this file which extends the default SimpactII.  We override
 * the "addAgents" method, adding syphilisAgents instead of regular Agents to the
 * system. 
 * 
 * (3) Third, in the constructor we call super(), which makes the default version
 * of the SimpactII. Change the InfectionOperator through anonymous classing. This
 * new infection operator does everything the original does (because it calls
 * super() at the beginning, but performs the steps necessary for syphilis infection
 * as well. 
 * 
 */
public class WithSyphilis extends SimpactII{
    
    public double syphilisInfectivity = 0.3;
    public int intialNumberSyphilisInfected = 5;
    
    public WithSyphilis(){
        super();
        infectionOperator = new InfectionOperator() {
            
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

            public void performInitialInfections(SimpactII state) { 
                super.performInitialInfections(state); //perform initial HIV infections AND syphilis infections
                for (int i = 0; i < intialNumberSyphilisInfected; i++) {
                    SyphilisAgent agent = (SyphilisAgent) state.myAgents.get(state.random.nextInt(state.population));
                    agent.syphilisWeeksInfected = 1;
                }
            }
        };
    }
    
    public void addAgents(){
        addNAgents(population,SyphilisAgent.class);
    }
    
    public static void main (String[] args){
        WithSyphilis ws = new WithSyphilis();
        ws.run();
        ws.prevalence();
    }
    
    
}
