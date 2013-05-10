/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.Heuristics;

import CombinationPrevention.OptimizationProblems.OptimizationProblem;

/**
 *
 * @author visiting_researcher
 */
public interface Heuristic {
    
    public double[] solve(OptimizationProblem op);
    
}
