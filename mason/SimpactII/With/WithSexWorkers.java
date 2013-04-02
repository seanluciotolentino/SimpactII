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
public class WithSexWorkers {
    
    public static void main (String[] args){
        SimpactII model = new SimpactII();
        model.addAgents(Agent.class, 200);
        model.addAgents(SexWorkerAgent.class, 20);
        model.run();
        model.prevalence();
    }
}
