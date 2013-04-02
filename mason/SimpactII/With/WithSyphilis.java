package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.SyphilisAgent;
import SimpactII.InfectionOperators.InfectionOperator;
import SimpactII.InfectionOperators.SyphilisInfectionOperator;
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
    
    public WithSyphilis(){
        infectionOperator = new SyphilisInfectionOperator(0.3, 5);//syphilis infectivity / initial number of infected
    }
    
    public void addAgents(){
        addNAgents(population,SyphilisAgent.class);
    }
    
    public static void main (String[] args){
        SimpactII model = new WithSyphilis();
        model.run();
        model.prevalence();
    }
    
    
}
