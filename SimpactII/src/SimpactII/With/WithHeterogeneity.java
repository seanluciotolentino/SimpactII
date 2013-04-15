/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.*;
import SimpactII.GUI;
import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class WithHeterogeneity {
    
    public static void main(String[] args){
        //make new model and add agents
        SimpactII s = new SimpactII();
        s.numberOfYears = 20;
        
        //make two scenarios:
        //scenario 1: just age agents
        s.addAgents(Agent.class, 1000); //base population
        s.run();
        s.prevalence();
        //s.agemixingScatter();
        
        //scenario 2: age agents, some 
//        s.resetPopulations();
//        s.addAgents(AgeAgent.class, 900);
//        s.addAgents(SugarDaddyAgent.class, 50); 
//        s.addAgents(SexWorkerAgent.class, 50);
//        s.run();
//        s.prevalence();
//        s.agemixingScatter();
        
    }
    
}
