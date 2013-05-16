package CombinationPrevention.OptimizationProblems;

import CombinationPrevention.Interventions.*;
import SimpactII.Agents.*;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.AIDSDeathTimeOperator;
import sim.util.Distributions.PowerLawDistribution;

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
            //init:
            {0,1,2, 3,4,5,  6,7,8,      //test and treat (0-8)
                
             9,  10,  11,  12,         //MC (9-14)
             
             13,14,  15,16,  17,18,  19,20,  21,22},//condom (15-24)
            
            //delta:
            {0.1, 50, 0.1, 50, 0.1, 50},   
            
            //min
            {1, 0, 1, 0, 1, 0},            
            
            //max
            {10,5000,10,5000,10,5000}};    
        this.X0 = parameters[0];
        this.delta = parameters[1];
        this.LB = parameters[2];
        this.UB = parameters[3];
    }
    
    public SimpactII setup(double[] args) {
        //basic simulation stuff        
        SimpactII s = new SimpactII();
        s.numberOfYears = 10;
        s.relationshipDurations = new PowerLawDistribution(-1.1);
        s.timeOperator = new AIDSDeathTimeOperator();
        s.infectionOperator = new InterventionInfectionOperator();
        
        //heterogenous population stuff
        int population = 1000; //make this larger when running actual simulations?
        s.addAgents(SexWorkerAgent.class, (int) (population * 0.04));
        s.addAgents(MSMAgent.class, (int) (population * 0.04));
        s.addAgents(BiAgent.class, (int) (population * 0.04));
        s.addAgents(BandAgeAgent.class, population - s.getPopulation()); //the rest are band age agents
        
        //intervention stuff    
        //Test and treat
        String[] targets = new String[] {"generalPopulation","msm","sexWorker"};
        for(int i = 0; i< targets.length; i++)
            s.addIntervention( new TestAndTreat(targets[i], args[i], args[i+1],args[i+2]));
        
        //MC
        targets = new String[] {"generalPopulation","msm","young","highRisk"};
        for(int i = 0; i< targets.length; i++)
            s.addIntervention( new MaleCircumcision(targets[i], args[9+i]));     
        
        //condom
        targets = new String[] {"generalPopulation","msm","sexWorker","young","highRisk"};
        for(int i = 0; i< targets.length; i++)
            s.addIntervention( new Condom(targets[i], args[13+i], args[13+i+2]));
        
        
        
        return s;
    }


}
