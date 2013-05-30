package CombinationPrevention;

import CombinationPrevention.OptimizationProblems.MultipleCombinationPrevention;
import SimpactII.Agents.*;
import SimpactII.DataStructures.Relationship;
import SimpactII.Distributions.Distribution;
import SimpactII.Distributions.PowerLawDistribution;
import SimpactII.Distributions.UniformDistribution;
import SimpactII.InfectionOperators.AIDSDeathInfectionOperator;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.DemographicTimeOperator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

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
    }

    public Validation() throws IOException{
        //make new model and add agents
        SimpactII s = new SimpactII();
        s.numberOfYears = 10;
        s.relationshipDurations = new PowerLawDistribution(-1.1);
        s.timeOperator = new DemographicTimeOperator();
        s.infectionOperator = new AIDSDeathInfectionOperator() ;
        
        //set the initial age distribution based on data
        s.ages = new Distribution() {
            //initial age population in 1980
            private double[] dist = new double[] {0.1549, 0.2941, 0.4155, 
                0.5198, 0.6118, 0.6905, 0.7543, 0.8088, 0.8545, 0.8932, 
                0.9247, 0.9495, 0.9690, 0.9825, 0.9915, 0.9964, 1.0000};
            private Distribution noise = new UniformDistribution(0, 4);
        
            @Override
            public double nextValue() {
                double r = Math.random();
                int i = 0;
                for(; r > dist[i]; i++){ continue; }
                return (i * 5) + noise.nextValue();
            }
        };
        
        //male cone agents
        HashMap attributes = new HashMap<String,Object>();
        attributes.put("preferredAgeDifference",0.9);
        attributes.put("probabilityMultiplier",-0.1);
        attributes.put("preferredAgeDifferenceGrowth",0.01);
        attributes.put("adDispersion",0.005);
        attributes.put("genderRatio", 1.0);        
        s.addAgents(ConeAgeAgent.class,400,attributes);
        
        s.addAgents(Agent.class,100); //50 men all over the place
        
        //half female band agents non-AD
        attributes.clear();
        attributes.put("genderRatio", 0.0); 
        attributes.put("offset",0.0);
        attributes.put("band",4.0);
        s.addAgents(BandAgeAgent.class,200,attributes);
        
        //other half of female agent AD forming
        attributes.put("offset",0.0);
        attributes.put("band",8.0);
        s.addAgents(BandAgeAgent.class,300,attributes);
                        
        //run the model
        s.run();
//        s.agemixingScatter();
        
        //write files
        bar(s);
        foo(s);
        System.exit(0);         
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

        //go through relationships and make the data
        int numAgents = s.myAgents.size();
        //double malePop = 0.0;
        //double femalePop = 0.0;
        double[] pop = new double[8];
        int[] hiv = new int[8];
        int[] ad = new int[8];
        for (int j = 0; j < numAgents; j++) {
            Agent a = (Agent) s.myAgents.get(j);
//            if(a.isMale())
//                malePop+=1;
//            else
//                femalePop+=1;

            //find agents group
            int group;
            int gender = (a.isMale())? 0:1;
            if(a.age < 24)
                group = 0;
            else if (a.age < 34)
                group = 2;
            else if (a.age < 44)
                group = 4;
            else
                group = 6;
            group+=gender; //shift gender plus one
            
            //go through all relationships
            boolean seen = false;
            int numRelations = s.myRelations.size();
            for (int k = 0; k < numRelations; k++) { //this might take a while...
                Relationship r = (Relationship) s.myRelations.get(k);
                if(!r.getAgent1().equals(a) && !r.getAgent2().equals(a))
                    continue;
                
                //add to population if unseen before
                if(!seen){
                    pop[group]++;
                    seen = true;
                }
                
                //if the relationship is age disparate
                double maleAge = r.getAgent1().isMale()? r.getAgent1().age: r.getAgent2().age;
                double femaleAge = r.getAgent1().isMale()? r.getAgent2().age: r.getAgent1().age;
                if(maleAge - femaleAge >= 5)    {
                    ad[group]++;
                    break;
                }
                
            }
            
            //check a's HIV status
            if(a.weeksInfected>0)
                hiv[group]++;
        }
        //after going through population, go through int[] and write to file
        for(int g =0 ; g <= 7 ; g+=2){
            prevHIV.write(g + "," + (hiv[g]/pop[g]) + "\n");
            prevAD.write(g + "," + (ad[g]/pop[g]) + "\n");
        }
        for(int g =1 ; g <= 7 ; g+=2){
            prevHIV.write(g + "," + (hiv[g]/pop[g]) + "\n");
            prevAD.write(g + "," + (ad[g]/pop[g]) + "\n");
        }
        prevHIV.flush();
        prevHIV.close();
        prevAD.flush();
        prevAD.close();
    }
    
}
