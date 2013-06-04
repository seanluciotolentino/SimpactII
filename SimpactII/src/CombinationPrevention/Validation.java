package CombinationPrevention;

import CombinationPrevention.Interventions.Condom;
import CombinationPrevention.OptimizationProblems.MultipleCombinationPrevention;
import SimpactII.Agents.*;
import SimpactII.DataStructures.Relationship;
import SimpactII.Distributions.*;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.DemographicTimeOperator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * Program that uses the same scenario as the combination prevention stuff
 * and output validate.
 * 
 */
public class Validation {
    
    String filename;
    String directory = "C:\\Users\\visiting_researcher\\Dropbox\\SACEMA"
                + "\\SIMPACT\\Abstracts and Papers (in production)\\SimpactII Paper"
                + "\\SimulatedData\\Simulated";
    
    public static void main(String[] args) throws IOException{
        new Validation();
        
        //test my degree distribution
//        int num = 1000;
//        //Distribution dist = new PowerLawDistribution(-2);   
//        final double cut = 8;        
//        final double alpha = 5;
//        final MersenneTwisterFast r = new MersenneTwisterFast();
//        Distribution dist = new PowerLawDistribution(cut, alpha, r);
//        System.out.print("hist([ ");
//        for(int i = 0; i < num; i++){
//            System.out.print(Math.round(dist.nextValue()) + ", ");
//            //System.out.print(dist.nextValue() + ", ");
//        }
//        //System.out.println("],0:5:length(x)*5); plot(bins/sum(bins)); hold all; plot(x)");
//        System.out.println("],0:10); ");

    }

    public Validation() throws IOException{
        //make new model and add agents
        final SimpactII s = new SimpactII();
        s.numberOfYears = 30;
        s.relationshipDurations = new PowerLawDistribution(52,4.2,s.random);
        s.degrees = new PowerLawDistribution(8, 10, s.random);
        s.timeOperator = new DemographicTimeOperator();
        InfectionOperator io = new AIDSDeathInfectionOperator() ;
        io.transmissionProbability = 0.008;
        io.initialNumberInfected = 2;
        s.infectionOperator = new InterventionInfectionOperator(io);
        
                
        //set the initial age distribution based on data
        s.ages = new Distribution() {
            //initial age population in 1985
            private double[] dist = new double[] {0.1489, 0.2830, 0.4048, 0.5113,
                0.6028, 0.6835, 0.7524, 0.8077, 0.8545, 0.8931, 0.9250, 0.9501,
                0.9689, 0.9827, 0.9914, 0.9965, 1.0000};
            private Distribution noise = new UniformDistribution(0, 4,s.random);
        
            @Override
            public double nextValue() {
                double r = Math.random();
                int i = 0;
                for(; r > dist[i]; i++){ continue; }
                return (i * 5) + noise.nextValue();
            }
        };
        
        //condoms started to be used more in year 15
        //proportion using condoms: 10 --> 0.1% of the population
        //                          20 --> 0.2%
        //                          200 --> 2%
        //                          1500 --> 15%
        Intervention condom = new Condom("generalPopulation", 2500, 5){
            public double getStart(){ return 52*10; };
            public double getSpend(){ return 0.0; };
        };
        s.addIntervention(condom);
        condom = new Condom("generalPopulation", 2500, 5){
            public double getStart(){ return 52*20; };
            public double getSpend(){ return 0.0; };
        };
        s.addIntervention(condom);
        
        //>>>>>>> ADD AGENTS
        int pop =1000;
        
        //male cone agents
        HashMap attributes = new HashMap<String,Object>();
        attributes.put("preferredAgeDifference",0.9);
        attributes.put("probabilityMultiplier",-0.1);
        attributes.put("preferredAgeDifferenceGrowth",0.02);
        attributes.put("adDispersion",0.005);
        attributes.put("genderRatio", 1.0);        
        s.addAgents(ConeAgeAgent.class, (int) ((pop/2) * 0.9),attributes);
                        
        s.addAgents(Agent.class,(int) ((pop/2) * 0.1)); //50 men all over the place
        
        //half female band agents non-AD
        attributes.clear();
        attributes.put("preferredAgeDifference",2.0);
        attributes.put("probabilityMultiplier",-0.9);
        attributes.put("preferredAgeDifferenceGrowth",0.01);
        attributes.put("genderRatio", 0.0);        
        s.addAgents(TriAgeAgent.class,pop/4,attributes);
        
        //other half of female agent AD forming
        attributes.put("preferredAgeDifference",2.0);
        attributes.put("probabilityMultiplier",-0.5);
        attributes.put("preferredAgeDifferenceGrowth",0.01);
        s.addAgents(TriAgeAgent.class,pop/4,attributes);
        //<<<<<<<<<<< ADD AGENTS
                
        //write files        
        foo(s);
        bar(s);
        
        //debug runs
//        s.run();        
//        s.agemixingScatter();
//        s.demographics();                
//        s.prevalence();
        //System.exit(0);         
    }
    
    private void bar(SimpactII s) throws IOException{
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
            
            ////skip Male Male relationships
            if(r.getAgent1().isMale() && r.getAgent2().isMale())
                continue;
            
            //skip non-traditional relationships
            if(r.getAgent1().getClass() == SexWorkerAgent.class && r.getAgent2().getClass() == SexWorkerAgent.class)
                continue;
            if(r.getAgent1().getClass() == MSMAgent.class && r.getAgent2().getClass() == MSMAgent.class)
                continue;

            //age mixing file
            if (r.getAgent1().isMale()) {
                ageMixing.write(r.getAgent1AgeAtFormation()+ "," + r.getAgent2AgeAtFormation() + "\n");
            } else {
                ageMixing.write(r.getAgent2AgeAtFormation() + "," + r.getAgent1AgeAtFormation() + "\n");
            }

            //relationship duration file
            relationDuration.write((r.getEnd() - r.getStart()) + "\n");
        }
        ageMixing.close();
        relationDuration.close();
    }
    
    private void foo(SimpactII s) throws IOException{
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
    
}
