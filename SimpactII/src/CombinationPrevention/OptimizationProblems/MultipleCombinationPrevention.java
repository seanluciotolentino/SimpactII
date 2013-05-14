package CombinationPrevention.OptimizationProblems;

import SimpactII.Agents.*;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SimpactII.Interventions.Condom;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.AIDSDeathTimeOperator;

/**
 *
 * @author Lucio Tolentino
 * 
 * Combination Prevention optimization problem for multiple HIV prevention
 * programs. Every intervention starts at year 2 (week 2*52 = 104).  
 * 
 */
public class MultipleCombinationPrevention extends OptimizationProblem{

    public MultipleCombinationPrevention(String metric){
        super(metric);        
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
        //basic simulation stuff        
        SimpactII s = new SimpactII();
        s.numberOfYears = 10;
        s.timeOperator = new AIDSDeathTimeOperator();
        s.infectionOperator = new InterventionInfectionOperator();
        
        //heterogenous population stuff
        int population = 1000; //make this larger when running actual simulations?
        s.addAgents(SexWorkerAgent.class, (int) (population * 0.04));
        s.addAgents(MSMAgent.class, (int) (population * 0.04));
        s.addAgents(BiAgent.class, (int) (population * 0.04));
        s.addAgents(BandAgeAgent.class, population - s.getPopulation()); //the rest are band age agents
        
        
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
