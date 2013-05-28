/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.OptimizationProblems;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class OptimizationProblem {

    protected int averageOver = 5;
    protected final double BUDGET = 400_000.0;// 50 million = $5,000/person year
    private String metric;
    protected int population;
    protected double[] X0;
    protected double[] delta;
    protected double[] LB;
    protected double[] UB;
    private double numberInfections;
    private double numberDeaths;

    public OptimizationProblem(String metric, int population) {
        this.population = population;
        this.metric = metric;
    }

    public double run(double[] combination) {
        //run combination n times and add to results bag
        ExecutorService executor = Executors.newFixedThreadPool(averageOver); //for parallelization
        Bag results = new Bag(averageOver);
        for (int j = 0; j < averageOver; j++) {
            final SimpactII s = setup(combination);
            Callable run = new Callable(){
                @Override
                public Object call() {
                    s.run();
                    return s;
                }
            };
            results.add(executor.submit(run));
        }
        executor.shutdown();
        
        //average over the results bag when they are all done
        double avg = 0;
        for (int j = 0; j < averageOver; j++) {
            try {
                SimpactII s = (SimpactII) ((Future) results.get(j)).get();
                avg += goodness(combination, s);
            } catch (InterruptedException ex) {
                Logger.getLogger(OptimizationProblem.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(OptimizationProblem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //s.prevalence();
        return avg / averageOver;
        
        //SimpactII s = setup(combination);
        //double avg = 0;
        //for (int j = 0; j < averageOver; j++) {
        //    s.run();
        //    avg += goodness(combination, s);
        //}
        ////s.prevalence();
        //return avg / averageOver;
    }
    
    public SimpactII setup(double[] combination) {
        return new SimpactII(); //this should be overwritten
    }
    
    public double cost(double[] args, SimpactII s){
        return -1; //this should also be overwritten
    }
    
    public double goodness(double[] args, SimpactII s) {
        double goodness = (cost(args,s) > BUDGET)? cost(args,s): 0;
        switch ( getMetric() ) {
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

    private double totalLifeYears(SimpactII s){
        //returns the total number of life years which occured in the simulation
        double lifeYears = 0;
        double now = 52*30; //this shouldn't be so strict but schedule.getTime() returns infinity... 
        //go through agents and tally
        int numAgents = s.myAgents.size();
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) s.myAgents.get(i);
            double addition = agent.getTimeOfAddition();
            double removal = Math.min(now,agent.timeOfRemoval);
            lifeYears += removal - addition;
        }
        return lifeYears;
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

    /**
     * @return the metric
     */
    public String getMetric() {
        return metric;
    }

    /**
     * @param metric the metric to set
     */
    public void setMetric(String metric) {
        this.metric = metric;
    }
}
