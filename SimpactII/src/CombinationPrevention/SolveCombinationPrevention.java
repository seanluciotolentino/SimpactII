package CombinationPrevention;

import CombinationPrevention.Heuristics.*;
import CombinationPrevention.OptimizationProblems.*;

/**
 *
 * @author visiting_researcher
 */
public class SolveCombinationPrevention {

    public static void main(String[] args) throws InterruptedException {
        String metric = "DALYs";
        
        //set optimization problem
        OptimizationProblem ccp = new CondomCombinationPrevention(metric);

        //set heuristic
        //Heuristic h = new SimulatedAnnealing();
        Heuristic h = new Genetic();

        //run it
        //double[] solution = h.solve(ccp);
        double[] solution = ccp.getX0();
        //double[] solution = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

        //display metrics
        System.err.println("======");
        for (int i = 0; i < solution.length; i++) {
            System.err.println(solution[i]);
        }
        System.out.println(metric + ": " + ccp.run(solution));
    }
}
