/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention;

import CombinationPrevention.Heuristics.SimulatedAnnealing;
import CombinationPrevention.Heuristics.Heuristic;
import CombinationPrevention.OptimizationProblems.CondomCombinationPrevention;
import CombinationPrevention.OptimizationProblems.OptimizationProblem;

/**
 *
 * @author visiting_researcher
 */
public class SolveCombinationPrevention {
        
    public static void main(String[] args){
        //set optimization problem
        OptimizationProblem ccp = new CondomCombinationPrevention("totalInfections");
        
        //set heuristic
        Heuristic h = new SimulatedAnnealing();
        
        //run it
        double[] solution = h.solve(ccp);
        
        //display metrics
        System.err.println("======");
        for(int i = 0; i < solution.length; i++){
            System.err.println(solution[i]);
        }
    }
    
}
