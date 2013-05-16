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
import SimpactII.TimeOperators.AIDSDeathTimeOperator;



/**
 *
 * @author visiting_researcher
 */
public class CondomCombinationPrevention extends OptimizationProblem{
    
    public CondomCombinationPrevention(String metric,int population){
        super(metric,population);        
        double[][] parameters = {
            {2, 100, 2.5, 100, 3, 100},        //init
            {0.1, 50, 0.1, 50, 0.1, 50},    //delta
            {1, 0, 1, 0, 1, 0},             //min
            {10,5000,10,5000,10,5000}};    //max
        this.X0 = parameters[0];
        this.delta = parameters[1];
        this.LB = parameters[2];
        this.UB = parameters[3];
    }
    
    public SimpactII setup(double[] combination) {
        SimpactII s = new SimpactII();
        s.numberOfYears = 10;
        s.timeOperator = new AIDSDeathTimeOperator();
        s.infectionOperator = new InterventionInfectionOperator();
        
        //set up condom interventions
        for(int i = 0; i <= 4; i+=2){
            Condom c = new Condom();
            c.condomInfectivityReduction = 0.99;   
            c.howMany = 104;
            c.start = combination[i] * 54;
            c.spend = combination[i+1];
            s.addIntervention(c);
        }
        return s;
    }
}
