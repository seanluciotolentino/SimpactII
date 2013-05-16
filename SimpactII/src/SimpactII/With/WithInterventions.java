/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import CombinationPrevention.Interventions.*;
import SimpactII.Agents.*;
import SimpactII.InfectionOperators.*;
import SimpactII.SimpactII;
import sim.util.Distributions.*;

/**
 *
 * @author visiting_researcher
 */
public class WithInterventions {
    
    public static void main (String[] args){
        //test CondomCP
        SimpactII s = new SimpactII();
        s.numberOfYears = 10;
        s.infectionOperator = new InterventionInfectionOperator();
        //Condom ccp = new Condom("young",5000,1);
        //ccp.howMany = 20;
        //s.addIntervention(ccp);
        TestAndTreat tat = new TestAndTreat("generalPopulation", 10000, 1000, 0.1);
        tat.dropoutTimes = new Distribution() { //make it so no one drops out
           @Override
            public double nextValue() {
                return 52*10;
            }
        };
        s.addIntervention(tat);
        
        s.run();
        s.prevalence();
        System.out.println("total cost = " + tat.getSpend());
        
        ////test the exponential
        //double x0 = 52;
        //double lambda = 0.5;
        //Distribution dist = new ExponentialDecay(x0,lambda);
        //int num = 500;
        //System.out.print("hist([ ");
        //for(int i = 0; i < num; i++)
        //    System.out.print(Math.round(dist.nextValue()) + ", ");
        //System.out.println("],0:52:(52*10)); title(\'x0 = " + x0 + ",lambda = " + lambda + "\')");
        ////System.out.println("],0:52:(52*10))/" + num);
       

    }
    
}
