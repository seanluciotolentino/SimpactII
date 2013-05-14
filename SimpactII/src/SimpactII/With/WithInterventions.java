/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import CombinationPrevention.Interventions.Condom;
import SimpactII.Agents.*;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.*;
import SimpactII.SimpactII;
import java.util.HashMap;
import sim.util.Distributions.UniformDistribution;

/**
 *
 * @author visiting_researcher
 */
public class WithInterventions {
    
    public static void main (String[] args){
        //test CondomCP
        SimpactII s = new SimpactII();
        s.numberOfYears = 50;
        s.infectionOperator = new InterventionInfectionOperator();
        Condom ccp = new Condom("young",5000,1);
        ccp.howMany = 20;
        s.addIntervention(ccp);
        s.run();
        s.prevalence();
        System.out.println("total cost = " + ccp.getSpend());
       

    }
    
}
