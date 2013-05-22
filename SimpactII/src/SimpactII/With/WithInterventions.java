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
    
    public static void main (String[] args_){
        ////test CondomCP
        //SimpactII s = new SimpactII();
        //s.numberOfYears = 10;
        //s.infectionOperator = new InterventionInfectionOperator();
        ////Condom ccp = new Condom("young",5000,1);
        ////ccp.howMany = 20;
        ////s.addIntervention(ccp);
        //TestAndTreat tat = new TestAndTreat("generalPopulation", 10000, 1000, 0.1);
        //tat.dropoutTimes = new Distribution() { //make it so no one drops out
        //   @Override
        //    public double nextValue() {
        //        return 52*10;
        //    }
        //};
        //s.addIntervention(tat);
        //
        //s.run();
        //s.prevalence();
        //System.out.println("total cost = " + tat.getSpend());
        
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
       
        //basic simulation stuff        
        SimpactII s = new SimpactII();
        s.numberOfYears = 30;
        s.relationshipDurations = new PowerLawDistribution(-1.1);
        s.infectionOperator = new InterventionInfectionOperator(new AIDSDeathInfectionOperator() );
        
        //heterogenous population stuff        
        int population = 1000;
        s.addAgents(SexWorkerAgent.class, (int) (population * 0.04));
        s.addAgents(MSMAgent.class, (int) (population * 0.04));
        s.addAgents(BiAgent.class, (int) (population * 0.04));
        s.addAgents(BandAgeAgent.class, (int) population - s.getPopulation()); //the rest are band age agents
        
        //intervention stuff  
        //{0,1,2, 3,4,5,  6,7,8,                    //test and treat (0-8)
        // 9,  10,  11,  12,                        //MC (9-14)
        // 13,14,  15,16,  17,18,  19,20,  21,22},  //condom (15-24)
        
        //Test and treat
//        double[] args = new double[]{100,100,0.5, 100,100,0.5,  100,100,0.5,      //test and treat (0-8)
//             10,  10,  10,  10,         //MC (9-14)
//             100,4,  100,4,  100,4,  100,4,  100,4};//condom (15-24);
        double[] args = new double[24];
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
        
        //run it and produce output
        s.run();
        s.prevalence();
        //s.agemixingScatter();
    }
    
}
