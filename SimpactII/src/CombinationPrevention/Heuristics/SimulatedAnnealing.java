/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.Heuristics;

import CombinationPrevention.Heuristics.Heuristic;
import CombinationPrevention.OptimizationProblems.OptimizationProblem;
import SimpactII.SimpactII;
import ec.util.MersenneTwisterFast;

/**
 *
 * @author visiting_researcher
 */
public class SimulatedAnnealing implements Heuristic{
    
    private MersenneTwisterFast rand;
    
    public SimulatedAnnealing() {
        rand = new MersenneTwisterFast();
        //you have to construct a simulated annealing solver so to speak.
    }
    
    public double[] solve(OptimizationProblem op){
        //initialization
        double[] X0 = op.getX0();
        double[] delta = op.getDelta();
        double[] min = op.getLB();
        double[] max = op.getUB();

        //setup
        double[] bestState , state ;
        bestState = state = X0;
        double bestEnergy , energy ;
        bestEnergy = energy = op.run(X0);
        int k = 0;
        int kMax = 10;
        
        //mainloop
        while (energy > 0.001 && k < kMax) {
            double T = temperature(k);
            double[] newstate = neighbor(state, delta, min, max, k);
            double newenergy  = op.run(newstate);
            
            if (probability(energy, newenergy, T) > rand.nextDouble()) {
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
    
    private double probability(double e, double enew, double T) {
        if (enew < e) {
            return 1.0;
        } else {
            return Math.exp((e - enew) / T);
        }
    }

    private double temperature(int k) {
        //%this function determines the temperature of the system at step k. This
        //%should be changed relative to the problem and kMax.
        //return Math.pow(.96, k);
        return Math.pow(.8, k);
    }

    private double[] neighbor(double[] state, double[] delta, double[] min, double[] max, int k) {
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
