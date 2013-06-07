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
public class CombinationPrevention implements OptimizationProblem {

    public int averageOver = 1;
    public final double BUDGET = 400_000.0; //$40 / person / year
    public String metric;
    public int population;

    public CombinationPrevention(String metric, int population) {
        this.metric = metric;
        this.population = population;
    }

    public double[] getX0() {
        //init:
        return new double[]{100, 1, 0.1, 100, 10, 0.1, 100, 10, 0.1, //test and treat (0-8)
            10, 0, 0, 0, //MC (9-14)
            100, 4, 100, 0, 100, 0, 100, 0, 100, 0};//condom (15-24)
    }

    public double[] getDelta() {
        //delta:
        return new double[]{10, 10, 0.05, 10, 10, 0.05, 10, 10, 0.05, //test and treat (0-8)
            10, 10, 10, 10, //MC (9-14)
            10, 1, 10, 1, 10, 1, 10, 1, 20, 1};   //condom (15-24)  
    }

    public double[] getLB() {
        return new double[]{0, 0, 0.01, 0, 0, 0.01, 0, 0, 0.01, //test and treat (0-8)
            0, 0, 0, 0, //MC (9-14)
            0, 1, 0, 1, 0, 1, 0, 1, 0, 1};    //condom (15-24) 
    }

    public double[] getUB() {
        return new double[]{1000, 100, 0.99, 1000, 100, 0.99, 1000, 100, 0.99, //test and treat (0-8)
            50, 50, 50, 50, //MC (9-14)
            1000, 52, 1000, 52, 1000, 52, 1000, 52, 1000, 52};   //condom (15-24)   
    }

    public double run(double[] combination) {
        //run combination n times and add to results to average
        SimpactII s = setup(combination);
        double avg = 0;
        for (int j = 0; j < averageOver; j++) {
            s.run();
            avg += goodness(combination, s);
        }
        //s.prevalence();
        return avg / averageOver;
    }

    public SimpactII setup(double[] args) {
        //basic simulation stuff
        SimpactII s = new ValidatedModel(population);

        //intervention stuff  
        //{0,1,2, 3,4,5,  6,7,8,                    //test and treat (0-8)
        // 9,  10,  11,  12,                        //MC (9-14)
        // 13,14,  15,16,  17,18,  19,20,  21,22},  //condom (15-24)

        //Test and treat
        String[] targets = new String[]{"generalPopulation", "msm", "sexWorker"};
        for (int i = 0; i < targets.length; i++) {
            s.addIntervention(new TestAndTreat(targets[i], args[(3 * i)], args[(3 * i) + 1], args[(3 * i) + 2]));
        }

        //MC
        targets = new String[]{"generalPopulation", "msm", "young", "highRisk"};
        for (int i = 0; i < targets.length; i++) {
            s.addIntervention(new MaleCircumcision(targets[i], args[i + 9]));
        }

        //condom
        targets = new String[]{"generalPopulation", "msm", "sexWorker", "young", "highRisk"};
        for (int i = 0; i < targets.length; i++) {
            s.addIntervention(new Condom(targets[i], args[(2 * i) + 13], args[(2 * i) + 13 + 1]));
        }

        return s;
    }

    public double goodness(double[] args, SimpactII s) {
        double goodness = (cost(args, s) > BUDGET) ? cost(args, s) : 0;
        switch (metric) {
            case "totalInfections":
                goodness += totalInfections(s);
                break;
            case "totalDeaths":
                goodness += totalDeaths(s);
                break;
            case "totalLifeYears":
                goodness += totalLifeYears(s);
                break;
        }
        return goodness;
    }

    public double cost(double[] args, SimpactII s) {
        double cost = 0;
        int numInterventions = s.myInterventions.size();
        for (int i = 0; i < numInterventions; i++) {
            Intervention I = (Intervention) s.myInterventions.get(i);
            cost += I.getSpend();
        }
        return cost;
    }

    private double totalInfections(SimpactII s) {
        //returns the total infections which occurred in the simualtion
        double totalInfections = 0;

        //go through agents and tally
        int numAgents = s.myAgents.size();
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) s.myAgents.get(i);
            totalInfections += (agent.weeksInfected >= 1) ? 1 : 0; //add if you were infected
        }
        return totalInfections;
    }

    private double totalDeaths(SimpactII s) {
        //returns the total number of deaths which occured in the simulation
        double totalDeaths = 0;

        //go through agents and tally
        int numAgents = s.myAgents.size();
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) s.myAgents.get(i);
            totalDeaths += (agent.timeOfRemoval <= Double.MAX_VALUE) ? 1 : 0; //add if you were infected
        }
        return totalDeaths;
    }

    private double totalLifeYears(SimpactII s) {
        //returns the total number of life years which occured in the simulation
        double lifeYears = 0;
        double now = 52 * 30; //this shouldn't be so strict but schedule.getTime() returns infinity... 
        //go through agents and tally
        int numAgents = s.myAgents.size();
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) s.myAgents.get(i);
            double addition = agent.getTimeOfAddition();
            double removal = Math.min(now, agent.timeOfRemoval);
            lifeYears += removal - addition;
        }
        return lifeYears;
    }
    
    public void print(SimpactII s, double[] combo){
        //top bar stuff
        System.err.println(metric + ":," + goodness(combo, s) + ",");
        double totalCost = cost(combo, s);
        System.err.println("Cost:, " + totalCost + " ,");
        
        //actual interventions        
        System.err.println("Parameter Value, Total Cost, Percentage of Budget");
        //Test and treat
        Intervention intervention; 
        double total = 0;
        String[] targets = new String[] {"generalPopulation","msm","sexWorker"};
        for(int i = 0; i< targets.length; i++){
            System.err.println(combo[(3*i)] + ", ," );
            System.err.println(combo[(3*i)+1]   + ", ," );
            System.err.println(combo[(3*i)+2]   + ", ," );
            
            //intervention total
            intervention = (Intervention) s.myInterventions.get(i);
            System.err.print("," + intervention.getSpend() );
            total+=intervention.getSpend();
            System.err.println(", " + intervention.getSpend() / totalCost);
        }
        System.err.println("," + total + "," + total / totalCost);
        
        //MC
        total = 0;
        targets = new String[] {"generalPopulation","msm","young","highRisk"};
        for(int i = 0; i< targets.length; i++){
            System.err.println(combo[i+9]     + ", " );
                        
            //intervention total
            intervention = (Intervention) s.myInterventions.get(i+3);
            System.err.print("," + intervention.getSpend() );
            total+=intervention.getSpend();
            System.err.println(", " + intervention.getSpend() / totalCost);
        }
        System.err.println("," + total + "," + total / totalCost);
        
        //condom
        total = 0;
        targets = new String[] {"generalPopulation","msm","sexWorker","young","highRisk"};
        for(int i = 0; i< targets.length; i++){
            System.err.println(combo[(2*i)+13]     + ", " );
            System.err.println(combo[(2*i)+14]   + ", " );
            
            //intervention total
            intervention = (Intervention) s.myInterventions.get(i+7);
            double thisCost = intervention.getSpend() ;
            System.err.print("," + thisCost);
            total+=thisCost;
            System.err.println(", " + thisCost / totalCost);
        }
        System.err.println("," + total + "," + total / totalCost);
    }
}
