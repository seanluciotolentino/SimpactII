/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.OptimizationProblems;

import CombinationPrevention.OptimizationProblems.OptimizationProblem;
import SimpactII.Agents.Agent;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SimpactII.Interventions.Condom;
import SimpactII.SimpactII;



/**
 *
 * @author visiting_researcher
 */
public class CondomCombinationPrevention extends OptimizationProblem{
 
    //Simulation parameters       
    private int numberOfYears = 10;
    
    public CondomCombinationPrevention(String metric){
        super(metric);        
        double[][] parameters = {
            {2, 50, 2.5, 50, 3, 50},        //init
            {0.1, 50, 0.1, 50, 0.1, 50},    //delta
            {1, 0, 1, 0, 1, 0},             //min
            {10,1000,10,1000,10,1000}};    //max
        this.X0 = parameters[0];
        this.delta = parameters[1];
        this.LB = parameters[2];
        this.UB = parameters[3];
    }
    
    public SimpactII setup(double[] combination) {
        SimpactII s = new SimpactII();
        s.numberOfYears = numberOfYears;
        s.infectionOperator = new InterventionInfectionOperator();
        
        //set up condom interventions
        Condom c = new Condom();
        c.condomInfectivityReduction = 0.99;        
        for(int i = 0; i <= 4; i+=2){
            c.start = combination[i];
            c.spend = combination[i+1];
            s.addIntervention(c);
        }
        return s;
    }
}
