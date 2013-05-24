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
        String metric = "DALYs";//args[0];//
        int population = 1000;//Integer.parseInt(args[1]);//
        System.out.println("metric: " + metric + " population: " + population);
        
        //set optimization problem
        //OptimizationProblem op = new CondomCombinationPrevention(metric);
        OptimizationProblem op = new MultipleCombinationPrevention(metric,population);

        //set heuristic
        //Heuristic h = new SimulatedAnnealing();
        Heuristic h = new Genetic();

        //run it
        double[] solution = h.solve(op);
        //double[] solution = op.getX0();
        //double[] solution = loadSolution();
        
        //display metrics
        System.err.println("======");
        System.err.print("{");
        for (int i = 0; i < solution.length; i++) {
            System.err.print(solution[i] + ", ");
        }
        System.err.println("}");
        System.err.println("Running for output...");
        SimpactII s = op.setup(solution);
        s.run();
        s.prevalence();
        System.out.println(metric + ": " + op.goodness(solution, s));
        System.out.println("cost = " + op.cost(solution, s));
    }
    
    private static double[] loadSolution(){
        return new double[] {372.2062786029771, 812.6642923500908, 0.09659884012126432, 517.3682196100041, 402.334351619127, 0.3759787628565957, 55.48525162749907, 831.6261506228556, 0.2984314321882403, 78.72582564454689, 601.4392635406008, 457.67069938298386, 468.06925889579276, 484.29391380518473, 12.560925835941068, 28.17109926920436, 6.845347017615481, 990.6070934599443, 6.7461570828401145, 334.37388064002437, 46.16227924074221, 780.5492405827679, 40.018410734918966, };
        
        
        //return new double[] {1000,1000,0.5, 0,0,0.5,  0,0,0.5,   0,  0,  0,  0,  0,0,  0,0,  0,0,  0,0,  0,0};
        //return new double[] {1000,1000,0.5, 0,0,0.5,  0,0,0.5,   0,  0,  0,  0,  0,0,  0,0,  0,0,  0,0,  0,0};
    }
}
