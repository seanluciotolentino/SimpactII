/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.Interventions;

import SimpactII.Interventions.Intervention;
import sim.engine.SimState;

/**
 *
 * @author visiting_researcher
 */
public class TestAndTreat implements Intervention{
    
    //settable parameters
    private String target;
    private double numSlots;
    private double numTests;
    private double retentionRate;
    
    //parameters that are set for the combination prevention paper
    public double start = 2.0*52;
    public int howMany = 10;
    
    public TestAndTreat(String target, double numSlots, 
            double numTests, double retentionRate){
        this.target = target;
        this.numSlots = numSlots;
        this.numTests = numTests;
        this.retentionRate = retentionRate;
    }

    @Override
    public void step(SimState s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {
        return -1; //???
    }
    
}
