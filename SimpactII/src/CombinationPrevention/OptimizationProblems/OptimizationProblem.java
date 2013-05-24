/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.OptimizationProblems;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class OptimizationProblem {

    protected int averageOver = 1;
    protected final double BUDGET = 50000000.0;// 50 million = $5,000/person year
    protected String metric;
    protected int population;
    protected double[] X0;
    protected double[] delta;
    protected double[] LB;
    protected double[] UB;
    private double numberInfections;
    private double numberDeaths;

    public OptimizationProblem(String metric, int population) {
        this.population = population;
        //grab number of infections & deaths
        if(metric.equals("infectionsAverted")){
            System.err.println("Running baseline comparison...");
            this.metric = "totalInfections";
            numberInfections = run(new double[24]);            
        }
        
        if(metric.equals("deathsAverted")){
            System.err.println("Running baseline comparison...");
            this.metric = "totalDeaths";
            numberDeaths = run(new double[24]);
        }

        this.metric = metric;
    }

    public double run(double[] combination) {
        //average over some runs and return
        SimpactII s = setup(combination);
        double avg = 0;
        for (int j = 0; j < averageOver; j++) {
            s.run();
            avg += goodness(combination, s);
        }
        //s.prevalence();
        return avg / averageOver;
    }
    
    public SimpactII setup(double[] combination) {
        return new SimpactII(); //this should be overwritten
    }

    public double goodness(double[] args, SimpactII s) {
        if (cost(args,s) > BUDGET) {
            return Double.POSITIVE_INFINITY;
        } else {
            switch (metric) {
                case "totalInfections":
                    return totalInfections(s);
                case "totalDeaths":
                    return totalDeaths(s);
                case "infectionsAverted":
                    return infectionsAverted(s);
                case "deathsAverted":
                    return deathsAverted(s);
                case "DALYs":
                    return DALYs(s);
                case "multiComponent":
                    return multiComponent(s);
                default:
                    return -101.0; //this shouldn't happen but Java requires the statement
            }
        }
    }
    
    public double cost(double[] args, SimpactII s){
        return -1; //this should also be overwritten
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

    private double infectionsAverted(SimpactII s) {
        return totalInfections(s) - numberInfections; //this order because it's a minimization problem
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

    private double deathsAverted(SimpactII s) {
        return totalDeaths(s) - numberDeaths;
    }

    private double DALYs(SimpactII s) {
        //returns the total number of deaths which occured in the simulation
        double DALYs = 0;

        //go through agents and tally
        int numAgents = s.myAgents.size();
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) s.myAgents.get(i);
            if( agent.timeOfRemoval>=Double.MAX_VALUE || 
                    agent.getAge() > s.timeOperator.getMAX_AGE()  ){ continue;}
            DALYs += (s.timeOperator.getMAX_AGE() - agent.getAge() ); 
        }
        return DALYs;
    }

    private double multiComponent(SimpactII s) {
        return -1.0;
    }

    public double[] getX0() {
        return X0;
    }

    public double[] getDelta() {
        return delta;
    }

    public double[] getUB() {
        return UB;
    }

    public double[] getLB() {
        return LB;
    }
}
