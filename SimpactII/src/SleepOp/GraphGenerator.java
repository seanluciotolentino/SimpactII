package SleepOp;

import CombinationPrevention.ValidatedModel;
import SimpactII.Agents.Agent;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SimpactII.SimpactII;
import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author Lucio
 */
public class GraphGenerator {
    
    public static void main (String[] args){
        //graph1();
        //graph2();
        //graph3();
        //graph4();
        graph5();
    }
    
    /*
     * Graph1 produces a graph of population size versus runtime for the two
     * models (with and without sleeping component). 
     */
    public static void graph1(){
        //parameters for simulation
        int averageOver = 5;
        
        //instance var
        double start;
        SimpactII s;
        HashMap<String,Object> attributes = new HashMap();

        System.out.println("0, , , Baseline, Modified");
        for(int population = 500; population <= 2500; population+=500){
        //for(int population = 100; population <= 500; population+=100){
            double baseline = 0; //for averaging
            double modified = 0;            
            for(int run = 0; run < averageOver; run++){
                
                //baseline
                start = System.currentTimeMillis();
                s = new ValidatedModel(population);
                s.run();        
                double time1 = System.currentTimeMillis() - start;


                //sleeping algorithm
                start = System.currentTimeMillis();
                s = new SleepingValidatedModel(population);
                s.run();
                double time2 = System.currentTimeMillis() - start;

                //print times / add averages
                System.out.println("1, Run " + run + "," + population + ", " + time1 + ", " + time2 );
                baseline+=time1;
                modified+=time2;
            }
            System.out.println("0, Average," + population + ", " + (baseline/averageOver) + ", " + (modified/averageOver) );            
        }
        
    }
    
    /*
     * Graph2 shows that the approximation is appropriate -- we get similar results
     * for baseline and modified algorithm
     * 
     */
    public static void graph2(){
        //parameters for simulation
        int population = 1000;  
        
        //instance var
        SimpactII s;
        
        //run simulations
        //baseline
        s = new ValidatedModel(population);
        
        //display graphs
        for(int run = 0; run < 5; run++){
            s.run();
            System.out.print("0," + population + ", baseline"  + run + ",");
            PrintPrevalence(s);
        }
        PrintAgePrevalence(s);
        PrintDemographics(s);

        //sleeping algorithm
        s = new SleepingValidatedModel(population);
        
        //display graphs
        for(int run = 0; run < 5; run++){
            s.run();
            System.out.print("0," + population + ", modified"  + run + ",");
            PrintPrevalence(s);
        }
        PrintAgePrevalence(s);
        PrintDemographics(s);      
    }
    
    /*
     * Graph3 shows speed improvement as a function of initial prevalence
     */    
    public static void graph3(){
        //parameters for simulation
        int population = 1000;        
        
        //instance var
        double start;
        SimpactII s;
        
        //run simulations
        System.out.println("0, ,Prevalence, Speed Up");
        double[] initialPrevalence = {0.01, 0.05, 0.10, 0.15, 0.30, 0.5, 0.7, 0.9};
        for(int i = 0; i < initialPrevalence.length; i++){
            int initialNumberInfected = (int) (initialPrevalence[i]*population);
            double baselineT = 0; //for averaging
            double modifiedT = 0;
            int averageOver = 5;
            for(int run = 0; run < averageOver; run++){
                //baseline
                start = System.currentTimeMillis();
                s = new ValidatedModel(population);
                InterventionInfectionOperator iio = (InterventionInfectionOperator) s.infectionOperator;
                iio.parent.initialNumberInfected = initialNumberInfected;
                
                s.run();        
                double time1 = System.currentTimeMillis() - start;


                //sleeping algorithm
                start = System.currentTimeMillis();
                s = new SleepingValidatedModel(population);
                iio = (InterventionInfectionOperator) s.infectionOperator;
                iio.parent.initialNumberInfected = initialNumberInfected;
                s.run();
                double time2 = System.currentTimeMillis() - start;

                //print times / add averages
                System.out.println("2, Run " + run + "," + initialPrevalence[i] + ", " + time1 + ", " + time2 );
                baselineT+=time1;
                modifiedT+=time2;
            }
            baselineT/=averageOver;
            modifiedT/=averageOver;
            System.out.println("1, Average SpeedUp," + initialPrevalence[i]  + ", " + (baselineT/modifiedT) );            
        }
    }
    
    /*
     * Graph to show speedup as a function of simulation year
     */
    public static void graph4(){
        //parameters for simulation
        int population = 1000;
        final int printInterval = 2*52; //x * 52 = x years
        
        //instance var
        SimpactII s;
        
        //run simulations
        System.out.println("1, SimTime, RunTime");
        
        //baseline
        final double start1 = System.currentTimeMillis();
        s = new ValidatedModel(population){
            public void start() {
                super.start();
                schedule.scheduleRepeating(new Steppable(){
                    public void step(SimState ss) {
                        System.out.println("0," + ss.schedule.getTime() + ", " + ( System.currentTimeMillis() - start1));
                    }
                }, printInterval);
            }
            
        };
        s.run();
        System.out.print("1, Baseline Prevalence,");
        PrintPrevalence(s);


        //sleeping algorithm
        final double start2 = System.currentTimeMillis();
        s = new SleepingValidatedModel(population){
            public void start() {
                super.start();
                schedule.scheduleRepeating(new Steppable(){
                    public void step(SimState ss) {
                        System.out.println("0," + ss.schedule.getTime() + ", " + ( System.currentTimeMillis() - start2));
                    }
                }, printInterval);
            }
            
        };        
        s.run();     
        System.out.print("1, Modified Prevalence,");
        PrintPrevalence(s);
        
    }
    
    /*
     * Graph to show the benefit of parallelization and sleeping algorithm
     */
    private static void graph5() {
        //local vars
        double start;
        SimpactII s;
        int population = 1000;
        
        
        //scenario 1: baseline, sequential
        start = System.currentTimeMillis();
        s = new ValidatedModel(population); 
        s.processors = 1;//saying explicitly will de-paralellize
        s.run();
        System.out.println("0,Scenario 1, " + (System.currentTimeMillis() - start));
        
        //scenario 2: baseline, parallel
        start = System.currentTimeMillis();
        s.processors = Runtime.getRuntime().availableProcessors(); 
        s = new ValidatedModel(population); 
        s.run();
        System.out.println("0,Scenario 2, " + (System.currentTimeMillis() - start));
        
        //scenario 3: modified, sequential
        start = System.currentTimeMillis();
        s = new SleepingValidatedModel(population);
        s.processors = 1; 
        s.run();
        System.out.println("0,Scenario 3, " + (System.currentTimeMillis() - start));
        
        //scenario 4: modified, parallel
        start = System.currentTimeMillis();
        s = new SleepingValidatedModel(population);
        s.processors = Runtime.getRuntime().availableProcessors(); 
        s.run();
        System.out.println("0,Scenario 4, " + (System.currentTimeMillis() - start));
    }
    
        
    public static void PrintDemographics(SimpactII s){
        //vars for the graphic:
        int numBoxes = 9;
        int boxSize = 10;
        int timeGranularity = 52; //4 = 1 month, 52 = 1 year, etc...
        
        //do the calculations
        int[] demographic;
        int numAgents = s.myAgents.size();
        double now = Math.min(s.numberOfYears*52, s.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        for (int t = timeGranularity; t < now; t += timeGranularity) { //Go through each time step (skipping the first)
            demographic = new int[numBoxes]; //create an int[] with the number of slots we want
            
            //go through agents...
            for (int i = 0; i < numAgents; i++) { 
                Agent agent = (Agent) s.myAgents.get(i);
                if (agent.getTimeOfAddition() >= t || agent.timeOfRemoval < t){  continue; } //skip if the agent wasn't born yet or has been removed

                double age = agent.getAge()*52; //convert age to weeks
                double ageAtT = age - now + t;        
                ageAtT/= 52; //convert back to years
                int level = (int) Math.min(numBoxes-1, Math.floor( ageAtT / boxSize)); 
                demographic[ level ] += 1; //...and add them to their delineations level
            }
            
            //print the delineations for this time step
            System.out.print(t + ", ");
            for (int j = 0; j < numBoxes; j++) { 
                //data.addValue(demographic[j], j*boxSize + " - " + (((j+1)*boxSize)-1), t + "");
                System.out.print(demographic[j] + ",");
            }
            System.out.println();
        }
    }
    
    public static void PrintPrevalence(SimpactII s){
        int timeGranularity = 52; 
        int numAgents = s.myAgents.size(); //this were added to this data struct, but should not have been remove.
        double now = Math.min(s.numberOfYears*52, s.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        double population;
        double totalInfections;       
        for (int t = timeGranularity; t < now; t += timeGranularity) { //Go through each time step (skipping the first)
            //at each time step collect ALIVE POPULATION and NUMBER INFECTED
            population = 0;
            totalInfections = 0;
            
            //go through agents and tally
            for (int i = 0; i < numAgents; i++) { 
                Agent agent = (Agent) s.myAgents.get(i);
                double ageAtT = agent.age - (now - t)/52;
                double timeOfInfection = (now - agent.weeksInfected);
                
                if (agent.getTimeOfAddition() < t && agent.timeOfRemoval > t //if he or she is alive at this time step
                        && (ageAtT >=15 && ageAtT <50 ) ){ //AND within the right age range
                    //add him or her to population counts
                    population++;                    
                    
                    //tally him or her for prevalence counts
                    if (agent.weeksInfected>=1 && timeOfInfection < t){ //you are (1) infected AND (2) infected before time t
                        totalInfections++; 
                    }
                }                       
            }// end agents loop
            population = Math.max(1,population);
            System.out.print(totalInfections / population + ", ");
        }//end time loop    
        System.out.println();
    }
    
    public static void PrintAgePrevalence(SimpactII s){
        //make the data
        int[][] prevalence = new int[2][7];
        double[][] population = new double[2][7];
        int numAgents = s.myAgents.size(); 
        double t, now = Math.min(s.numberOfYears*52, s.schedule.getTime()); 
        for (int i = 0; i < numAgents; i++) { 
            Agent agent = (Agent) s.myAgents.get(i);
            int age = (int) Math.floor(agent.age/5)-3 ;  
            int gender = agent.isMale()? 0:1;
            if(age<0 || age>6 || agent.timeOfRemoval < now ){continue;} //skip childern
            
            //add to population counts
            population[gender][age]++;
            
            //add to prevalence counts (if applicable)
            if(agent.weeksInfected>0){ prevalence[gender][age]++;}
        }
            
        //print it all out
        double pop = 0;
        double prev = 0;
        System.out.println("1,Ages,Males,Females");
        for(int age = 0; age < 7; age++){
            //after everyone for this time step, add to the data
//            System.out.println("====age " + ((age+3)*5) + "======");
            System.out.print("1," + ((age+3)*5) + "-" + (((age+3)*5)+4) + "," + (prevalence[0][age] / population[0][age]) );
            System.out.println(", " + prevalence[1][age] / population[1][age]);
//            pop+=population[0][age]+population[1][age];
//            prev+=prevalence[0][age]+prevalence[1][age];
        }
    }
}
