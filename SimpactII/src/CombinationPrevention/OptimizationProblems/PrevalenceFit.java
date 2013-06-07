/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.OptimizationProblems;

import CombinationPrevention.Interventions.Condom;
import CombinationPrevention.ValidatedModel;
import SimpactII.Agents.Agent;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class PrevalenceFit implements OptimizationProblem{
    
    public int averageOver = 5;
    
    public PrevalenceFit(){        
        /*
         * PARAMETERS TO OPTIMIZE
         *  -introduction
         *  -start
         *  -stop
         *  -slant
         *  -max
         *  -infectivity          
          */  
        
        /* Simulated annealing returned:
            208.0   =   4
            416.0   =   8
            1222.0  =   23.5
            0.005 
            3500.0 
            0.01
         */
        
    }
    
    public double[] getX0() {//init:        
        return new double[]{4*52,  13*52,  19*52,  0.005,  3500,   0.01};//condom (15-24)
    }

    public double[] getDelta() {//delta:        
        return new double[]{26,    26,     26,     0.001,  100,    0.01};   //condom (15-24)  
    }

    public double[] getLB() {
        return new double[]{0,     0,      1,      0.001,  100,    0.001};    //condom (15-24) 
    }

    public double[] getUB() {
        return new double[]{5*52,  15*52,  30*52,  0.01,   4000,   0.05};   //condom (15-24)   
    }
    
    public double run(double[] combination) {
        //run combination n times and add to results to average
        SimpactII s = setup(combination);
        double avg = 0;
        for (int j = 0; j < averageOver; j++) {
            s.run();
            avg += goodness(s);
        }
        //s.prevalence();
        return avg / averageOver;
    }
    
    
    public SimpactII setup(double[] parameters){
        SimpactII s = new ValidatedModel(1000);
        s.myInterventions.clear(); //get rid of the other condom uptake
        s.infectionOperator.HIVIntroductionTime = (int) parameters[0];
        s.infectionOperator.transmissionProbability = parameters[5];     //looks good...?        
        final double start =    parameters[1];     //13 = 1998
        final double end =      parameters[2];      //15 = 2000, 20 = 2005, 18 = 2003
        final double slant =    parameters[3];     //looks good...?        
        final double min =      100;          //1%
        final double max =      parameters[4];        //30%        
        //solved constants
        final double a = slant*(max-min)/(end - start);
        final double b = (start + end)/2;
        Intervention i = new Condom("generalPopulation",10,5) {
            public void distributeCondoms(SimpactII state){
                double t = state.schedule.getTime() ;
                condomsPerInterval = (int) ((max-min)/(1+Math.exp( a*(b-t)))+min);
                super.distributeCondoms(state);
            }
            public double getStart() { return start; }
            public double getSpend() { return 0.0; }
        };
        s.addIntervention(i);
        return s;
    }
    
    public double goodness(SimpactII s) {
        //known prevalence:
        double[] actual = {0, 0, 0, 0, 0.005, 0.008, 0.013, 0.021, 0.033, 0.049, 0.069, 0.092, 0.114, 0.133, 0.148, 0.159, 0.166, 0.17, 0.172, 0.173, 0.173, 0.172, 0.172, 0.172, 0.173, 0.173};
        double goodness = 0;
        
        //actually calculate prevalence
        int timeGranularity = 52; 
        int numAgents = s.myAgents.size(); //this were added to this data struct, but should not have been remove.
        double now = Math.min(s.numberOfYears*52, s.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        double population;
        double totalInfections;
        for (int t = timeGranularity; t < 26*52; t += timeGranularity) { //Go through each time step (skipping the first)
            //at each time step collect ALIVE POPULATION and NUMBER INFECTED
            population = 0;
            totalInfections = 0;
            
            //go through agents and tally
            for (int i = 0; i < numAgents; i++) { 
                Agent agent = (Agent) s.myAgents.get(i);
                double timeOfInfection = (now - agent.weeksInfected);
                
                if (agent.getTimeOfAddition() < t && agent.timeOfRemoval > t //if he or she is alive at this time step
                        && (agent.age>15 || agent.age<50 ) ){ //AND within the right age range
                    //add him or her to population counts
                    population++;                    
                    
                    //tally him or her for prevalence counts
                    if (agent.weeksInfected>=1 && timeOfInfection < t){ //you are (1) infected AND (2) infected before time t
                        totalInfections++; 
                    }
                }                       
            }// end agents loop
            //System.out.print(totalInfections / population + ", ");
            double act = actual[t/timeGranularity];
            double sim = (totalInfections / population);
            goodness += Math.abs( sim - act);
        }//end time loop    
        //System.out.println();
        return goodness;
    }
    
    
    
}
