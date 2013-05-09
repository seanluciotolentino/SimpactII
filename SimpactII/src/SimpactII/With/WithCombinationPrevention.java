/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SimpactII.Interventions.*;
import SimpactII.SimpactII;
import ec.util.MersenneTwisterFast;

/**
 *
 * @author Lucio Tolentino
 *
 * Proof of concept: Using the end point prevalence as a metric, can we use
 * simulated annealing to find a good combination of prevention. Future work
 * should 1) Use more sophisticated interventions 2) Use more sophisticated
 * heuristics
 *
 */
public class WithCombinationPrevention {

    private MersenneTwisterFast rand = new MersenneTwisterFast();
    private final int BUDGET = 5000;    
    private int holdAtOneTime = 20;
    private int numberOfYears = 10;
    private double averageOver = 10.0;

    public static void main(String[] args) {
        WithCombinationPrevention wcp = new WithCombinationPrevention();
    }
    
    public WithCombinationPrevention(){
        //set up parameters to optimize
        double[][] variables = {
            {2, 50, 2.5, 50, 3, 50}, //init
            {0.1, 50, 0.1, 50, 0.1, 50}, //delta
            {1, 0, 1, 0, 1, 0}, //min
            {10, 1000, 10, 1000, 10, 1000},};   //max
        
        //run the SA
        double[] solved = SimulatedAnnealing(variables);
        //double[] solved = {0, 0, 0, 0, 0, 0};
        System.err.println("======");
        for(int i = 0; i < solved.length; i++){
            System.err.println(solved[i]);
        }
        
        //display differences
        System.err.println("======");
        double[] output = run(variables[0]);
        double cost = output[0];
        double prev = output[1];
        System.out.println("ORIGINAL cost: " + cost + " prevalence: " + prev);
        
        output = run(solved);
        cost = output[0];
        prev = output[1];
        System.out.println("SOLVED cost: " + cost + " prevalence: " + prev);
        
        //graphs of differences
        SimpactII s1 = setup(variables[0]);
        s1.run();
        s1.prevalence();
        
        SimpactII s2 = setup(solved);
        s2.run();
        s2.prevalence();
        
        
    }

    public double goodness(double[] output) {
        //how good is the output from the model?
        double cost = output[0];
        double prev = output[1];
        if (cost > BUDGET) {
            return Integer.MAX_VALUE; //infinity
        } else {
            return prev;
        }
    }
    
    private SimpactII setup(double[] args){
        SimpactII s = new SimpactII();
        s.numberOfYears = numberOfYears;
        
        //set up condom interventions
        
        Condom condom0 = new Condom(args[0],args[1]);
        condom0.condomInfectivityReduction = 0.99;
        condom0.howMany = holdAtOneTime;
        s.addIntervention(condom0);

        Condom condom1 = new Condom(args[2],args[3]);
        condom1.howMany = holdAtOneTime;
        condom1.condomInfectivityReduction = 0.99;
        s.addIntervention(condom1);

        Condom condom2 = new Condom(args[4],args[5]);
        condom2.howMany = holdAtOneTime;
        condom2.condomInfectivityReduction = 0.99;
        s.addIntervention(condom2);
        
        s.infectionOperator = new InterventionInfectionOperator();
        return s;
    }

    public double[] run(double[] args) {
        SimpactII s = setup(args);

        //run the model
        
        double prev = 0;
        for(int i = 0; i < averageOver; i++){
            s.run();
            prev += metric(s);
        }
        prev = prev / averageOver;

        //calculate the cost
        double cost = args[1] + args[3] + args[5];


        return new double[]{cost, prev};
    }

    public double metric(SimpactII state) {
        //returns the prevalence at the end of the simulation
        double now = Math.min(state.numberOfYears * 52, state.schedule.getTime()); //determine if we are at the end of the simulation or in the middle
        double t = now; //time of interest

        int numAgents = state.myAgents.size();
        double population = 0;
        double totalInfections = 0;

        population = 0;

        totalInfections = 0;

        //go through agents and tally
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) state.myAgents.get(i);
            double timeOfInfection = (now - agent.weeksInfected);

            if (agent.getTimeOfAddition() < t && agent.timeOfRemoval > t) { //if he or she is alive at this time step
                population++;
                //tally him or her for prevalence counts
                if (agent.weeksInfected >= 1 && timeOfInfection < t) //you are (1) infected AND (2) infected before time t
                {
                    totalInfections++;
                }
            }
        }
        return totalInfections / population;
    }

    public double[] SimulatedAnnealing(double[][] variables) {
        //initialization
        double[] parameters = variables[0];
        double[] delta = variables[1];
        double[] min = variables[2];
        double[] max = variables[3];


        double[] state = parameters;
        double[] current = run(parameters);
        double energy = goodness(current);

        double[] bestState = state;
        double bestEnergy = energy;
        int k = 0;
        int kMax = 100;
        while (energy > 0.001 && k < kMax) {
            double T = temperature(k);
            double[] newstate = neighbor(state, delta, min, max, k);
            current = run(newstate);
            double newenergy = goodness(current);
            
            double p = probability(energy, newenergy, T);
            if (p > rand.nextDouble()) {
                System.out.println(k + " state transition: " + newenergy);
                
                state = newstate;
                energy = newenergy;
            } else {
                System.out.println(k + " state transition not made: " + newenergy);
            }

            if (energy < bestEnergy) {
                bestState = state.clone();
                bestEnergy = energy;
            }

            k = k + 1;
        }

        return bestState;

    }

    public double probability(double e, double enew, double T) {
        if (enew < e) {
            return 1.0;
        } else {
            return Math.exp((e - enew) / T);
        }
    }

    public double temperature(int k) {
        //%this function determines the temperature of the system at step k. This
        //%should be changed relative to the problem and kMax.
        //return Math.pow(.96, k);
        return Math.pow(.8, k);
    }

    public double[] neighbor(double[] state, double[] delta, double[] min, double[] max, int k) {
        int selection = rand.nextInt(state.length);

        //allows a change (in either direction) relative to temperature of s
        int ca = (int) Math.ceil(4 * temperature(k)); //4 is max step size?
        int direction = rand.nextBoolean()? 1: -1;
        double changeamount = direction * ca;

        double[] newState = state.clone();
        newState[selection] += changeamount * delta[selection];

        //fix parameters that are out of range
        if (newState[selection] > max[selection] || newState[selection] < min[selection])//if you failed, try try again
        {
            return neighbor(state, delta, min, max, k);
        }

        return newState;
    }
}
