/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.Condom;
import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class WithInterventions {
    
    public static void main (String[] args){
        //base stuff
        SimpactII s = new SimpactII(new Long(2));
        s.addAgents(Agent.class, 1000);
        s.numberOfYears = 20;
        
        //before intervention
        s.infectionOperator = new InfectionOperator(0.05);//(0.03);
        s.run();
        s.prevalence();
        
        //after intervention
        s.infectionOperator = new InterventionInfectionOperator(s.infectionOperator);// change the infection operator to consider interventions
        s.addIntervention(new Condom(2,1000));
        s.run();
        s.prevalence();
        

        

    }
    
}
