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
public class WithSexWorkers extends SimpactII {

    public int numSexWorkers = 20;

    public void addAgents() {
        //add basic agents
        addNAgents(population, Agent.class);
        //and then add sex worker agents in addition
        addNAgents(numSexWorkers, SexWorkerAgent.class);
    }
    
    public static void main (String[] args){
        SimpactII model = new WithSexWorkers();
        model.population = 2000;
        model.run();
        model.prevalence();
    }
}
