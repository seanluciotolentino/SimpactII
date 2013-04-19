/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.*;
import SimpactII.DataStructures.Relationship;
import SimpactII.SimpactII;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import sim.util.Distributions.PowerLawDistribution;

/**
 *
 * @author visiting_researcher
 */
public class WithHeterogeneity {
    
    public static void main(String[] args) throws IOException{
        //make new model and add agents
        SimpactII s = new SimpactII();
        s.numberOfYears =5;
        s.relationshipDurations = new PowerLawDistribution(-1.1);
        s.infectionOperator.initialNumberInfected = 80;
        HashMap attri = new HashMap<String,Object>();

        //make my heterogenous population
        attri.put("genderRatio", 0.0);
        s.addAgents(Agent.class, 300,attri); //add 200 lady agents
        attri.put("genderRatio", 0.5);
        attri.put("adAge", 15.0);
        s.addAgents(ExtremeAgeAgent.class, 700,attri);
        attri.put("adAge", 25.0);
        s.addAgents(ExtremeAgeAgent.class, 200,attri);
        attri.put("adAge", 35.0);
        s.addAgents(ExtremeAgeAgent.class, 200,attri);
        attri.put("adAge", 45.0);
        s.addAgents(ExtremeAgeAgent.class, 200,attri);
        attri.put("adAge", 55.0);
        s.addAgents(ExtremeAgeAgent.class, 200,attri);
                        
        //run the model
        s.run();
        
        //write files
        String filename;
        String directory = "C:\\Users\\visiting_researcher\\Dropbox\\SACEMA"
                + "\\SIMPACT\\Abstracts and Papers (in production)\\SimpactII Paper"
                + "\\SimulatedData\\Simulated";
            
        //write relationship  -- custom CSVs to make the graphs I want
        //age mixing
        filename = directory + "AgeMixing.csv";
        BufferedWriter ageMixing = new BufferedWriter(new FileWriter(filename));
        ageMixing.write("maleAge,femaleAge\n");   

        //durations
        filename = directory + "Durations.csv";
        BufferedWriter relationDuration = new BufferedWriter(new FileWriter(filename));
        relationDuration.write("relationshipDuration\n");
        
        //prevalence HIV
        filename = directory + "PrevHIV.csv";
        BufferedWriter prevHIV = new BufferedWriter(new FileWriter(filename));
        prevHIV.write("group,count\n");   

        //prevalence AD (>5 year age difference)
        filename = directory + "PrevAD.csv";
        BufferedWriter prevAD = new BufferedWriter(new FileWriter(filename));
        prevAD.write("group,count\n");

        //go through relationships and make the data
        int numRelations = s.myRelations.size();
        for (int j = 0; j < numRelations; j++) {
            Relationship r = (Relationship) s.myRelations.get(j);

            //age mixing file
            if (r.getAgent1().isMale()) {
                ageMixing.write(r.getAgent1AgeAtFormation()+ "," + r.getAgent2AgeAtFormation() + "\n");
            } else {
                ageMixing.write(r.getAgent2AgeAtFormation() + "," + r.getAgent1AgeAtFormation() + "\n");
            }

            //relationship duration file
            relationDuration.write((r.getEnd() - r.getStart()) + "\n");
        }
        
        //go through relationships and make the data
        int numAgents = s.myAgents.size();
        double malePop = 0.0;
        double femalePop = 0.0;
        int[] hiv = new int[8];
        int[] ad = new int[8];
        for (int j = 0; j < numAgents; j++) {
            Agent a = (Agent) s.myAgents.get(j);
            if(a.isMale())
                malePop+=1;
            else
                femalePop+=1;

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
            
            //check a's relationships for age disparity
            for (int k = 0; k < numRelations; k++) { //this might take a while...
                Relationship r = (Relationship) s.myRelations.get(k);
                if(!r.getAgent1().equals(a) && !r.getAgent2().equals(a))
                    continue;
                
                if (Math.abs(r.getAgent1AgeAtFormation()- r.getAgent2AgeAtFormation()) > 5){
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
            prevHIV.write(g + "," + (hiv[g]/malePop) + "\n");
            prevAD.write(g + "," + (ad[g]/malePop) + "\n");
        }
        for(int g =1 ; g <= 7 ; g+=2){
            prevHIV.write(g + "," + (hiv[g]/femalePop) + "\n");
            prevAD.write(g + "," + (ad[g]/femalePop) + "\n");
        }
        
        
        prevHIV.flush();
        prevHIV.close();
        prevAD.flush();
        prevAD.close();
        
        System.exit(0);         
    }
    
}
