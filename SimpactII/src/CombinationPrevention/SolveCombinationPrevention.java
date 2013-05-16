package CombinationPrevention;

import CombinationPrevention.Heuristics.*;
import CombinationPrevention.OptimizationProblems.*;
import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class SolveCombinationPrevention {

    public static void main(String[] args) throws InterruptedException {
        //set parameters
        String metric = "infectionsAverted";//args[0];//
        int population = 1000;//Integer.parseInt(args[1]);//
        System.out.println("metric: " + metric + " population: " + population);
        
        //set optimization problem
        //OptimizationProblem op = new CondomCombinationPrevention(metric);
        OptimizationProblem op = new MultipleCombinationPrevention(metric,population);

        //set heuristic
        //Heuristic h = new SimulatedAnnealing();
        Heuristic h = new Genetic();

        //run it
        //double[] solution = h.solve(ccp);
        //double[] solution = op.getX0();
        double[] solution = new double[24];

        //display metrics
        System.err.println("======");
        for (int i = 0; i < solution.length; i++) {
            System.err.println(solution[i]);
        }
        System.err.println("Running for output...");
        SimpactII s = op.setup(solution);
        s.run();
        s.prevalence();
        System.out.println(metric + ": " + op.goodness(solution, s));
        System.out.println("cost = " + op.cost(solution, s));
    }
}
