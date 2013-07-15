package CombinationPrevention;

import CombinationPrevention.Interventions.*;
import CombinationPrevention.OptimizationProblems.CombinationPrevention;
import CombinationPrevention.OptimizationProblems.PrevalenceFit;
import SimpactII.Agents.*;
import SimpactII.DataStructures.Relationship;
import SimpactII.Distributions.*;
import SimpactII.Graphs.AgePrevalence;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.DemographicTimeOperator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * Program that uses the same scenario as the combination prevention stuff
 * and output validate.
 * 
 */
public class ValidatedModel extends SimpactII{
    
    public static void main(String[] args) {
        SimpactII s = new ValidatedModel(1000);
        //basic runs
//        s.prevalence();
//        s.demographics();
//        new AgePrevalence(s);
        
        //run multiple times
        for (int i = 0; i < 20; i++){
            s.run();
            System.out.print("Run " + i + " :");
            PrintPrevalence(s);
        }
        
        //write files for matlap figure  
//        try{
//            WriteHIVADPrevalence(s);
//            WriteAgeMixingRelationshipDurations(s);
//        }catch(IOException ioe){
//            System.err.println(ioe);
//            System.exit(-1);
//        }
//
//        //print stuff for demographic / prevalence
//        System.out.println("DEMOGRAPHICS:");
//        PrintDemographics(s);
//        System.out.println("PREVALENCE:");
//        PrintPrevalence(s);
//        s.prevalence();
        
//        int stepSize = 100;
//        int numberRuns = 100;
//        int initialSeed = 1_000_000;
//        for(int seed = initialSeed; seed < (stepSize*numberRuns)+initialSeed; seed+=stepSize){
//            s.run(new String[] {"-seed",seed+""});
//            System.out.print("a,Seed, " + seed + ", ");
//            System.out.print("Quality, " + PrevalenceFit.goodness(s) + ", ");
//            PrintPrevalence(s);
//        }
        
    }

    public ValidatedModel(int population) {
        //basic model parameters
        numberOfYears = 30;
        relationshipDurations = new PowerLawDistribution(52,4.2,random);
        degrees = new PowerLawDistribution(8, 10, random);
        ages = new Distribution() {
            //initial age population in 1985
            private double[] dist = new double[] {0.1489, 0.2830, 0.4048, 0.5113,
                0.6028, 0.6835, 0.7524, 0.8077, 0.8545, 0.8931, 0.9250, 0.9501,
                0.9689, 0.9827, 0.9914, 0.9965, 1.0000};
            private Distribution noise = new UniformDistribution(0, 4,random);            
            @Override
            public double nextValue() {
                double r = Math.random();
                int i = 0;
                for(; r > dist[i]; i++){ continue; }
                return (i * 5) + noise.nextValue();
            }
        };//set the initial age distribution based on data
        
        //modified operators
        timeOperator = new DemographicTimeOperator();
        AIDSDeathInfectionOperator io = new AIDSDeathInfectionOperator() ;
            io.transmissionProbability = 0.008;
            io.initialNumberInfected = 5;
            io.HIVIntroductionTime = 3.5*52;
            io.CD4AtInfection = new Normal(1500, 250, random);
            io.CD4AtDeath = new UniformDistribution(0, 100, random);
        infectionOperator = new InterventionInfectionOperator(io);
        
        //behavioural change of condoms: Parameters: start, stop, slant, max
        addCondomBehaviorChange(population);
        
        //ART uptake
        addARTIncrease(population);
        
        //add agents
        addHeterogeneousAgents(population);
        
    }
    
    /*
     * behavioural change of condoms: Parameters: start, stop, slant, max
     */
    private void addCondomBehaviorChange(int population){
        final double start = 540;//13*52;     //13 = 1998
        final double end = 750;//19 *52;      //15 = 2000, 20 = 2005, 18 = 2003
        final double min = 10;          //1%
        final double max = .35*10*population;//4500;        //30%
        final double slant = 0.008;//0.005;     //looks good...?        
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
        addIntervention(i);
    }
    
    private void addARTIncrease(int population){
        final double start = 17*52;     //17 = 2002
        final double end = 25 *52;      //15 = 2000, 20 = 2005, 25 = 2010, 
        final double min = 10;          //1 slot = 0.001 coverage (0.1%)
        final double max = .05*population;        //200 slots = 0.2 coverage (20%)
        final double slant = 0.03;     //looks good...?        
        //solved constants
        final double a = slant*(max-min)/(end - start);
        final double b = (start + end)/2;
        Intervention i = new TestAndTreat("generalPopulation",1,1,0.9) {
            
            private int CD4Threshold = 200;
            
            /*
             * CD4 threshold changes in 2011
             */
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
        addIntervention(i);
    }
    
    protected void addHeterogeneousAgents(int population){
        //add special agents first
        /*
         *  THESE AGENTS WERE REMOVED FOR TESTS WITH THE SLEEPING AGENT ALGORITHM
         * --SHOULD BE ADDED IN FOR OTHER MODELS
         */
//        addAgents(SexWorkerAgent.class, (int) (population*0.04));
//        addAgents(MSMAgent.class, (int) (population*0.04));
//        population = population - getPopulation(); //update number in population
        
        //add "normal" agents
        //male cone agents
        HashMap attributes = new HashMap<String,Object>();
        attributes.put("preferredAgeDifference",0.9);
        attributes.put("probabilityMultiplier",-0.1);
        attributes.put("preferredAgeDifferenceGrowth",0.05);
        attributes.put("adDispersion",0.004);
        attributes.put("genderRatio", 1.0);
        attributes.put("meanAgeFactor", -0.08);
        addAgents(ConeAgeAgent.class, (int) ((population/2) * 0.5),attributes);
                        
        addAgents(Agent.class,(int) ((population/2) * 0.5),attributes); //50 men all over the place
        
        //half female band agents non-AD
        attributes.clear();
        attributes.put("preferredAgeDifference",2.0);
        attributes.put("probabilityMultiplier",-0.9);
        attributes.put("preferredAgeDifferenceGrowth",0.01);
        attributes.put("genderRatio", 0.0);
        addAgents(TriAgeAgent.class,population/4,attributes);
        
        //other half of female agent AD forming
        attributes.put("preferredAgeDifference",2.0);
        attributes.put("probabilityMultiplier",-0.5);
        attributes.put("preferredAgeDifferenceGrowth",0.01);
        addAgents(TriAgeAgent.class,population/4,attributes);
    }
    
    public static void WriteAgeMixingRelationshipDurations(SimpactII s) throws IOException{
        String filename;
        String directory = "C:\\Users\\visiting_researcher\\Dropbox\\SACEMA"
                + "\\SIMPACT\\Abstracts and Papers (in production)\\SimpactII Paper"
                + "\\SimulatedData\\Simulated";
        
        //age mixing file
        filename = directory + "AgeMixing.csv";
        BufferedWriter ageMixing = new BufferedWriter(new FileWriter(filename));
        ageMixing.write("maleAge,femaleAge\n");  
        
        //durations
        filename = directory + "Durations.csv";
        BufferedWriter relationDuration = new BufferedWriter(new FileWriter(filename));
        relationDuration.write("relationshipDuration\n");
        
        //go through relationships and make the data
        int numRelations = s.myRelations.size();
        for (int j = 0; j < numRelations; j++) {
            Relationship r = (Relationship) s.myRelations.get(j);
                        
            //skip non-traditional relationships
            if(r.getAgent1().getClass() == SexWorkerAgent.class || r.getAgent2().getClass() == SexWorkerAgent.class)
                continue;
            if(r.getAgent1().getClass() == MSMAgent.class || r.getAgent2().getClass() == MSMAgent.class)
                continue;

            //age mixing file
            if (r.getAgent1().isMale()) {
                //System.out.println(r.getAgent2().getClass() + ", " + r.getAgent1().getClass());
                ageMixing.write(r.getAgent2AgeAtFormation()+ "," + r.getAgent1AgeAtFormation() + "\n");
            } else {
                //System.out.println(r.getAgent1().getClass() + ", " + r.getAgent2().getClass());
                ageMixing.write(r.getAgent1AgeAtFormation() + "," + r.getAgent2AgeAtFormation() + "\n");
            }

            //relationship duration file
            relationDuration.write((r.getEnd() - r.getStart()) + "\n");
        }
        ageMixing.close();
        relationDuration.close();
    }
    
    public static void WriteHIVADPrevalence(SimpactII s) throws IOException{
        String filename;
        String directory = "C:\\Users\\visiting_researcher\\Dropbox\\SACEMA"
                + "\\SIMPACT\\Abstracts and Papers (in production)\\SimpactII Paper"
                + "\\SimulatedData\\Simulated";
        
        //prevalence HIV
        filename = directory + "PrevHIV.csv";
        BufferedWriter prevHIV = new BufferedWriter(new FileWriter(filename));
        prevHIV.write("group,count\n");   

        //prevalence AD (>5 year age difference)
        filename = directory + "PrevAD.csv";
        BufferedWriter prevAD = new BufferedWriter(new FileWriter(filename));
        prevAD.write("group,count\n");
        
        //average over a few runs
        double[] adAvg = new double[8];
        double[] hivAvg = new double[8];
        int averageOver = 10;
        for(int count = 0; count < averageOver; count++){        
            //initialize counters
            s.run();
            
            System.out.println("PREVALENCE:");
            PrintPrevalence(s);
            
            double[] adPop = new double[8];
            int[] ad = new int[8];
            double[] hivPop = new double[8];
            int[] hiv = new int[8];

            //add attributes to the agents to keep track of who we've added
            Bag agents = s.network.getAllNodes();
            int numAgents = agents.size();        
            for (int i = 0; i < numAgents; i++) { //add syphilis weeks infected attribute to every one
                Agent agent = (Agent) agents.get(i);
                int gender = agent.isMale()? 0:1;

                //denominators
                agent.attributes.put("seen" + (gender + 0), false);
                agent.attributes.put("seen" + (gender + 2), false);
                agent.attributes.put("seen" + (gender + 4), false);
                agent.attributes.put("seen" + (gender + 6), false);

                //numerators
                agent.attributes.put("seenAD" + (gender + 0), false);
                agent.attributes.put("seenAD" + (gender + 2), false);
                agent.attributes.put("seenAD" + (gender + 4), false);
                agent.attributes.put("seenAD" + (gender + 6), false);
            }

            //go through agents and tally them accordingly
            for (int j = 0; j < numAgents; j++) {
                Agent a = (Agent) agents.get(j);
                if(a.getClass() == MSMAgent.class || a.getClass() == SexWorkerAgent.class)
                    continue;

                //tally for HIV prevalence
                int gender = a.isMale()? 0:1;
                int group = (int) Math.min(6,2*Math.floor((a.age-15) / 10)) + gender;
                if(group<0){continue;} //skip the kids
                hivPop[group]++;
                if(a.weeksInfected>0)
                    hiv[group]++;

                //go through all relationships (not just network) to find his/her relationships
                int numRelations = s.myRelations.size(); 
                for (int k = 0; k < numRelations; k++) { //this might take a while...
                    Relationship r = (Relationship) s.myRelations.get(k);
                    if(!r.getAgent1().equals(a) && !r.getAgent2().equals(a))
                        continue;
                    
                    //grab the agents
                    //Agent male = r.getAgent1().isMale()? r.getAgent1(): r.getAgent2();
                    //Agent female = r.getAgent1().isMale()? r.getAgent2(): r.getAgent1();

                    //grab the agents age at formation
                    double maleAge = r.getAgent1().isMale()? r.getAgent1AgeAtFormation(): r.getAgent2AgeAtFormation();
                    double femaleAge = r.getAgent1().isMale()? r.getAgent2AgeAtFormation(): r.getAgent1AgeAtFormation();

                    //grab the gender and group
                    double age = a.equals(r.getAgent1())? r.getAgent1AgeAtFormation(): r.getAgent2AgeAtFormation();
                    group = (int) Math.min(6,2*Math.floor((age-15) / 10)) + gender;

                    //add to denominator if applicable
                    if(!(boolean) a.attributes.get("seen" + group)){
                        adPop[group]++;
                        a.attributes.put("seen" + group,true);
                    }

                    //add to numerator if applicable                
                    if(maleAge - femaleAge >= 5 && !(boolean)a.attributes.get("seenAD"+group) ) {
                        ad[group]++;
                        a.attributes.put("seenAD" + group,true);
                    }

                }


            } //end agents for loop
            //after going through population, go through int[] and write to file
            System.out.println("Adding averages for " + count);
            for(int g =0 ; g <= 7 ; g+=2){
                hivAvg[g] += hiv[g]/hivPop[g];
                adAvg[g] += ad[g]/adPop[g];
            }
            for(int g =1 ; g <= 7 ; g+=2){
                hivAvg[g] += hiv[g]/hivPop[g];
                adAvg[g] += ad[g]/adPop[g];
            }
        } //end averageing for loop
            for(int g =0 ; g <= 7 ; g+=2){
                prevHIV.write(g + "," + (hivAvg[g]/averageOver) + "\n");
                prevAD.write(g + "," + (adAvg[g]/averageOver) + "\n");
            }
            for(int g =1 ; g <= 7 ; g+=2){
                prevHIV.write(g + "," + (hivAvg[g]/averageOver) + "\n");
                prevAD.write(g + "," + (adAvg[g]/averageOver) + "\n");
            }
            prevHIV.flush();
            prevHIV.close();
            prevAD.flush();
            prevAD.close();
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
}