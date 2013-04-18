/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.*;
import SimpactII.DataStructures.Relationship;
import SimpactII.GUI;
import SimpactII.SimpactII;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import sim.util.Bag;
import sim.util.Distributions.Distribution;
import sim.util.Distributions.PowerLawDistribution;
import sim.util.Distributions.UniformDistribution;

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
        s.infectionOperator.initialNumberInfected = 0;
        HashMap attri = new HashMap<String,Object>();
        int population = 1000;
        
        //add male agents
//        attri.put("prefAD", 0.6);
//        attri.put("probMult", -0.1);
//        attri.put("adGrowth", 2.0);
//        attri.put("adDispersion", 0.025);
//        attri.put("genderRatio", 0.5);
//        s.addAgents(ConeAgeAgent.class, population/2,attri);
        
        //add female agents
//        attri.clear();
//        attri.put("band", 5.0);
//        attri.put("offset", 5.0);
        int p = 200;
        attri.put("genderRatio", 0.0);
        s.addAgents(Agent.class, p,attri); //add 200 lady agents
        attri.put("genderRatio", 0.5);
        attri.put("adAge", 15.0);
        s.addAgents(ExtremeAgeAgent.class, p,attri);
        attri.put("adAge", 25.0);
        s.addAgents(ExtremeAgeAgent.class, p,attri);
        attri.put("adAge", 35.0);
        s.addAgents(ExtremeAgeAgent.class, p,attri);
        attri.put("adAge", 45.0);
        s.addAgents(ExtremeAgeAgent.class, p,attri);
        attri.put("adAge", 55.0);
        s.addAgents(ExtremeAgeAgent.class, p,attri);
        
//        s.launchGUI();
//        return;
        
        //run the model
        s.run();
        //s.agemixingScatter();
        //return;
        
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
        int[] hiv = new int[8];
        int[] ad = new int[8];
        for (int j = 0; j < numAgents; j++) {
            Agent a = (Agent) s.myAgents.get(j);

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
        double pop = population/2.0; //gender stratifed population
        for(int g =0 ; g <= 7 ; g++){
            prevHIV.write(g + "," + (hiv[g]/pop) + "\n");
            prevAD.write(g + "," + (ad[g]/pop) + "\n");
        }
        prevHIV.flush();
        prevHIV.close();
        prevAD.flush();
        prevAD.close();
        
        System.exit(0);         
    }
    
}
