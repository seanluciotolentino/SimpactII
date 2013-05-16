package CombinationPrevention.OptimizationProblems;

import CombinationPrevention.Interventions.*;
import SimpactII.Agents.*;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SimpactII.Interventions.Intervention;
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
    
   public MultipleCombinationPrevention(String metric, int population){
        super(metric,population);        
        double[][] parameters = {
            //init:
            {100,100,0.5, 100,100,0.5,  100,100,0.5,      //test and treat (0-8)
             10,  10,  10,  10,         //MC (9-14)
             100,4,  100,4,  100,4,  100,4,  100,4},//condom (15-24)
//            {0,0,0.5, 0,0,0.5,  0,0,0.5,      //test and treat (0-8)
//             0,  0,  0,  0,         //MC (9-14)
//             0,0,  0,0,  0,0,  0,0,  0,0},//condom (15-24)
            
            //delta:
            {10,10,0.05, 10,10,0.05,  10,10,0.05,       //test and treat (0-8)
             10,  10,  10,  10,                          //MC (9-14)
             10,1,  10,1,  10,1,  10,1,  20,1},    //condom (15-24)  
            
            //min
            {0,0,0.01, 0,0,0.01,  0,0,0.01,             //test and treat (0-8)
             0,  0,  0,  0,                          //MC (9-14)
             0,1,  0,1,  0,1,  0,1,  0,1},    //condom (15-24)           
            
            //max
            {1000,1000,0.99, 1000,1000,0.99,  1000,1000,0.99, //test and treat (0-8)
             1000,  1000,  1000,  1000,                          //MC (9-14)
             1000,24,  1000,24,  1000,24,  1000,24,  21,24}};   //condom (15-24)   
        
        this.X0 = parameters[0];
        this.delta = parameters[1];
        this.LB = parameters[2];
        this.UB = parameters[3];
        this.population = population;
    }
    
    public SimpactII setup(double[] args) {
        //basic simulation stuff        
        SimpactII s = new SimpactII();
        s.numberOfYears = 10;
        s.relationshipDurations = new PowerLawDistribution(-1.1);
        s.timeOperator = new AIDSDeathTimeOperator();
        s.infectionOperator = new InterventionInfectionOperator();
        
        //heterogenous population stuff        
        s.addAgents(SexWorkerAgent.class, (int) (population * 0.04));
        s.addAgents(MSMAgent.class, (int) (population * 0.04));
        s.addAgents(BiAgent.class, (int) (population * 0.04));
        s.addAgents(BandAgeAgent.class, (int) population - s.getPopulation()); //the rest are band age agents
        
        //intervention stuff  
        //{0,1,2, 3,4,5,  6,7,8,                    //test and treat (0-8)
        // 9,  10,  11,  12,                        //MC (9-14)
        // 13,14,  15,16,  17,18,  19,20,  21,22},  //condom (15-24)
        
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
            s.addIntervention( new Condom(targets[i], args[13+i], args[13+i+1]));
        
        
        
        return s;
    }
    
    public double cost(double[] args,SimpactII s){
        double cost = 0;
        int numInterventions = s.myInterventions.size();
        for(int i = 0; i < numInterventions; i++){
            Intervention I = (Intervention) s.myInterventions.get(i);
            cost+= I.getSpend();
        }
        return cost;            
    }


}
