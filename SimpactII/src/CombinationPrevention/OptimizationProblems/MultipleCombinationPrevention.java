package CombinationPrevention.OptimizationProblems;

import CombinationPrevention.Interventions.*;
import CombinationPrevention.ValidatedModel;
import SimpactII.Agents.*;
import SimpactII.Distributions.*;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.*;
import java.util.HashMap;

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
            {100,1,0.1, 100,10,0.1,  100,10,0.1,      //test and treat (0-8)
             10,  0,  0,  0,         //MC (9-14)
             100,4,  100,0,  100,0,  100,0,  100,0},//condom (15-24)
            
            //delta:
            {10,10,0.05, 10,10,0.05,  10,10,0.05,       //test and treat (0-8)
             10,  10,  10,  10,                          //MC (9-14)
             10,1,  10,1,  10,1,  10,1,  20,1},    //condom (15-24)  
            
            //min
            {0,0,0.01, 0,0,0.01,  0,0,0.01,             //test and treat (0-8)
             0,  0,  0,  0,                          //MC (9-14)
             0,1,  0,1,  0,1,  0,1,  0,1},    //condom (15-24)           
            
            //max
            {1000,100,0.99, 1000,100,0.99,  1000,100,0.99, //test and treat (0-8)
             50,  50,  50,  50,                          //MC (9-14)
             1000,52,  1000,52,  1000,52,  1000,52,  1000,52}};   //condom (15-24)   
        
        this.X0 = parameters[0];
        this.delta = parameters[1];
        this.LB = parameters[2];
        this.UB = parameters[3];
        this.population = population;
    }
   
    public SimpactII setup(double[] args) {
        //basic simulation stuff
        SimpactII s = new ValidatedModel(population);
        
        //intervention stuff  
        //{0,1,2, 3,4,5,  6,7,8,                    //test and treat (0-8)
        // 9,  10,  11,  12,                        //MC (9-14)
        // 13,14,  15,16,  17,18,  19,20,  21,22},  //condom (15-24)
        
        //Test and treat
        String[] targets = new String[] {"generalPopulation","msm","sexWorker"};
        for(int i = 0; i< targets.length; i++)
            s.addIntervention( new TestAndTreat(targets[i], args[(3*i)], args[(3*i)+1],args[(3*i)+2]));
        
        //MC
        targets = new String[] {"generalPopulation","msm","young","highRisk"};
        for(int i = 0; i< targets.length; i++)
            s.addIntervention( new MaleCircumcision(targets[i], args[i+9]));     
        
        //condom
        targets = new String[] {"generalPopulation","msm","sexWorker","young","highRisk"};
        for(int i = 0; i< targets.length; i++)
            s.addIntervention( new Condom(targets[i], args[(2*i)+13], args[(2*i)+13+1]));
        
        
        
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
