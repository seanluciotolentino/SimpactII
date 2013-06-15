/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.OptimizationProblems;

import CombinationPrevention.Interventions.Condom;
import CombinationPrevention.Interventions.TestAndTreat;
import CombinationPrevention.ValidatedModel;
import SimpactII.Agents.Agent;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class PrevalenceFit implements OptimizationProblem{
    
    public int averageOver = 10;
    
    public PrevalenceFit(){        
        /*
         * PARAMETERS TO OPTIMIZE
         * Condom:
         *  -introduction
         *  -start
         *  -stop
         *  -slant
         *  -max
         *  -infectivity          
         * 
         * ART:
         *  -end
         *  -max
         * 
          */  
        
        /*
         * 3.5 //introduction
         * 540 = 10.38  //condom start
         * 750 = 14.41 //condom end         * 
         * 0.0086 //slant
         * 4143 = 41.43%
         * 0.023 = //infectivity
         * 1490.9
         * 213.75  
         */
        
    }
    
    public double[] getX0() {//init:        
        return new double[]{4*52,  13*52,  19*52,  0.005,  .35,   0.01,    //condom (15-24)
            25*52, 0.05};
    }

    public double[] getDelta() {//delta:        
        return new double[]{26,    26,     26,     0.001,  0.01,    0.001,   //condom (15-24)  
            26,     0.01};
    }

    public double[] getLB() {
        return new double[]{0,     5*52,      6*52,      0.001,  0.01,    0.001,   //condom (15-24) 
            20*52,  0.01};    
    }

    public double[] getUB() {
        return new double[]{5*52,  15*52,  30*52,  0.01,   0.50,   0.050,   //condom (15-24)   
            30*52,  0.30};   
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
        
        //Condoms
        addCondomsBC(s,parameters);

        
        //ART
        addART(s,parameters);
        
        return s;
    }
    
    private void addCondomsBC(SimpactII s, double[] parameters){
        final double start =    parameters[1];     //13 = 1998
        final double end =      parameters[2];      //15 = 2000, 20 = 2005, 18 = 2003
        final double slant =    parameters[3];     //looks good...?        
        final double min =      100;          //1%
        final double max =      parameters[4]*10*1000;        //30%        
        //solved constants
        final double a = slant*(max-min)/(end - start);
        final double b = (start + end)/2;
        Intervention i = new Condom("generalPopulation",10,5) {
            private Bag condomUsers;
            private int user = 0;
            public void step(SimState state){
                SimpactII s = (SimpactII) state;
                condomUsers = s.myAgents;
                super.step(state);
            }
            protected Agent findAgent(SimpactII state){
                Agent a = (Agent) condomUsers.get(user);
                double now = state.schedule.getTime();
                for( ; a.timeOfRemoval< now && user < condomUsers.size();user++)
                    a = (Agent) condomUsers.get(user);
                user++;
                return a;
            }
            public void distributeCondoms(SimpactII state){
                double t = state.schedule.getTime() ;
                user = 0; //start over from the beginning of the list
                condomsPerInterval = (int) ((max-min)/(1+Math.exp( a*(b-t)))+min);
                super.distributeCondoms(state);
            }
            public double getStart() { return start; }
            public double getSpend() { return 0.0; }
        };
        s.addIntervention(i);
    }

    private void addART(SimpactII s, double[] parameters){
        final double start = 17*52;     //17 = 2002
        final double end = parameters[6];//25 *52;      //15 = 2000, 20 = 2005, 25 = 2010, 
        final double min = 10;          //1 slot = 0.001 coverage (0.1%)
        final double max = parameters[7]*1000;//;50;        //200 slots = 0.2 coverage (20%)
        final double slant = 0.03;     //looks good...?        
        //solved constants
        final double a = slant*(max-min)/(end - start);
        final double b = (start + end)/2;
        Intervention i = new TestAndTreat("generalPopulation",1,1,0.9) {
            private int CD4Threshold = 200;
            public void step(SimState state) {
                state.schedule.scheduleOnce(26.5*52,new Steppable() {
                    @Override
                    public void step(SimState ss) {
                        CD4Threshold = 350;
                    }
                });
                super.step(state);
            }
            public void testStep(SimpactII s) {
                //go through agents, looking for those with "low" CD4
                //note that this completely overrides previous testStep -- no call to super
                Bag myAgents = s.network.allNodes;
                for (int i = 0; i < myAgents.size(); i++) { //add syphilis weeks infected attribute to every one
                    Agent agent = (Agent) myAgents.get(i);
                    
                    //if agent is HIV-positive, calculate CD4
                    if(agent.weeksInfected >= 1 && !treatmentQueue.contains(agent)){
                        double now = s.schedule.getTime();
                        
                        //CD4 at infection
                        double CD4I = (double) agent.attributes.get("CD4AtInfection"); 
                        double toi = now - agent.weeksInfected; //time of infection
                        
                        //CD4 at (eventual) death
                        double CD4D = (double) agent.attributes.get("CD4AtDeath"); 
                        double tod = (Double) agent.attributes.get("AIDSDeath"); //do I have to do a getValue?
                        
                        //interpolate CD4 and add to the queue accordingly
                        double CD4 = CD4I + (CD4D - CD4I)*( (now - toi) / (tod - toi) );
                        if(CD4 < CD4Threshold) //threshold changes midway through 2011
                            treatmentQueue.add(agent);                        
                    }                        
                }//end agents for-loop
            }//end test
            public void treatStep(SimpactII s) {
                double t = state.schedule.getTime() ;
                numSlots = (int) ((max-min)/(1+Math.exp( a*(b-t)))+min);
                super.treatStep(s);
            }
            public double getStart() { return start; }
            public double getSpend() { return 0.0; }
        };
        s.addIntervention(i);
    }
    
    public static double goodness(SimpactII s) {
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
                double ageAtT = agent.age - (now - t)/52;
                double timeOfInfection = (now - agent.weeksInfected);
                
                if (agent.getTimeOfAddition() < t && agent.timeOfRemoval > t //if he or she is alive at this time step
                        && (ageAtT >=15 && ageAtT <50 )){ //AND within the right age range
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
