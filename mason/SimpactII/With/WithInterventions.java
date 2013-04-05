/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.*;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.*;
import SimpactII.SimpactII;
import sim.util.Distributions.UniformDistribution;

/**
 *
 * @author visiting_researcher
 */
public class WithInterventions {
    
    public static void main (String[] args){
        //base stuff
        SimpactII s = new SimpactII(new Long(3));
        s.addAgents(AgeAgent.class, 1000,new String[] {"5","5"});
        s.numberOfYears = 20;
        s.relationshipDurations = new UniformDistribution(24, 52*2);
        
        //NO INTERVENTIONS
//        s.infectionOperator = new InfectionOperator(0.05);//(0.03);
//        s.run();
//        s.prevalence();
        
        //WITH INTERVENTIONS
        s.infectionOperator = new InterventionInfectionOperator(s.infectionOperator);// change the infection operator to consider interventions
        
        //condoms
        //s.addIntervention(new Condom(2,10000000));
        
        //bcc
        //s.addIntervention(new BehavioralChange(2,10));
        
        //test and treat
        s.addIntervention(new HIVTestAndCounsel(10, 1000));
//        ARVTreatment intervention = new ARVTreatment(10.0001, 500*1000);
//        intervention.timeTillNormalInfectivity = 52*20;//let's say no one drops out
//        intervention.ARVInfectivityReduction = 0.9999;
//        s.addIntervention(intervention);
        
        //male circumcision
//        MaleCircumcision MCC = new MaleCircumcision(10, 25000);
//        MCC.circumcisionInfectivityReduction = 0.99;
//        s.addIntervention(MCC);
        
        s.run();
        s.prevalence();
        

        

    }
    
}
