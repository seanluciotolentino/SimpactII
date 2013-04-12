package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.MSMAgent;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
import static sim.engine.SimState.doLoop;
import sim.util.Double2D;

/**
 *
 * @author Lucio Tolentino
 * 
 * This model is an example of how you can use different types of Agents in addition
 * to the basic agent. Here we create MSM agents and basic Agents. MSM agents are
 * identical to basic agents except that they are men who seek exclusively men. 
 * We inherit from SimpactII and then change the TimeOperator (via anonymous 
 * classing) so that no one is ever removed.  
 * 
 * Note that this class contains no main method. This means that it inherits the 
 * main function from SimpactII. 
 * 
 */
public class WithMSM {
    
    public static void main (String[] args){
        SimpactII model = new SimpactII(); 
        model.addAgents(Agent.class, 100);
        model.addAgents(MSMAgent.class, 20);
        model.run();
    }
}
