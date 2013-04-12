/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Interventions;

import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author visiting_researcher
 */
public interface Intervention extends Steppable {

    public void step(SimState s);

    public double getStart();

    public double getSpend();
}
