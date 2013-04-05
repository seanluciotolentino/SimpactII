/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.*;
import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class WithInterventions {
    
    public static void main (String[] args){
        //base stuff
        SimpactII s = new SimpactII(new Long(3));
        s.addAgents(Agent.class, 1000);
        s.numberOfYears = 20;
        
        //before intervention
        s.infectionOperator = new InfectionOperator(0.05);//(0.03);
        s.run();
        s.prevalence();
        
        //after intervention
        s.infectionOperator = new InterventionInfectionOperator(s.infectionOperator);// change the infection operator to consider interventions
        //s.addIntervention(new Condom(2,10000000));
        //s.addIntervention(new BehavioralChange(2,10));
        s.addIntervention(new HIVTest(2, 1000));
        ARVTreatment intervention = new ARVTreatment(2.0001, 500*1000);
        intervention.timeTillNormalInfectivity = 52*20;//let's say no one drops out
        intervention.ARVInfectivityReduction = 0.9999;
        s.addIntervention(intervention);
        s.run();
        s.prevalence();
        

        

    }
    
}
