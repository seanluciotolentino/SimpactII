package CombinationPrevention;

import CombinationPrevention.Heuristics.*;
import CombinationPrevention.OptimizationProblems.*;

/**
 *
 * @author visiting_researcher
 */
public class SolveCombinationPrevention {

    public static void main(String[] args) throws InterruptedException {
        //set parameters
        String metric = args[0];//"DALYs";
        int population = Integer.parseInt(args[1]);//1000;
        System.out.println("metric: " + metric + " population: " + population);
        
        //set optimization problem
        //OptimizationProblem op = new CondomCombinationPrevention(metric);
        OptimizationProblem op = new MultipleCombinationPrevention(metric,population);

        //set heuristic
        //Heuristic h = new SimulatedAnnealing();
        Heuristic h = new Genetic();

        //run it
        //double[] solution = h.solve(ccp);
        double[] solution = op.getX0();
        //double[] solution = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

        //display metrics
        System.err.println("======");
        for (int i = 0; i < solution.length; i++) {
            System.err.println(solution[i]);
        }
        System.out.println(metric + ": " + op.run(solution));
    }
}
