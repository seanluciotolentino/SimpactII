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
public interface OptimizationProblem {

    public double[] getX0();

    public double[] getDelta();

    public double[] getUB();

    public double[] getLB();
    
    public double run(double[] parameters);
    
}
