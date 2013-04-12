/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Interventions;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Distributions.UniformDistribution;

/**
 *
 * @author visiting_researcher
 */
public class BehavioralChange implements Intervention{
    
    public double start;
    public double spend;
    public double durationIncrease;
    
    public BehavioralChange( double startYear , double spend ) {
        this.start = startYear*52; //convert from year to weeks
        this.spend = spend;
    }

    @Override
    public void step(SimState s) {
        SimpactII state = (SimpactII) s;
        
        //change the distribution of relationships... maybe add some more sophistication to this
        state.relationshipDurations=new UniformDistribution(52,100);        
    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {
        return spend;
    }
}
