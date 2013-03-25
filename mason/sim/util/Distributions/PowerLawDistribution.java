/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util.Distributions;

import ec.util.MersenneTwisterFast;

/**
 *
 * @author visiting_researcher
 */
public class PowerLawDistribution implements Distribution{
    
    public double alpha;
    public double cut;
    private MersenneTwisterFast mtf;
    
    public PowerLawDistribution(double alpha, double cut){
        this.mtf = new MersenneTwisterFast();
        this.alpha = alpha;
        this.cut = cut;               
    }
    
    public PowerLawDistribution(double alpha, double cut, long seed){
        this.mtf = new MersenneTwisterFast(seed);
        this.alpha = alpha;
        this.cut = cut;               
    }

    public double nextValue() {
        return sim.util.distribution.Distributions.nextPowLaw(alpha, cut, mtf);
    }
    
    
    
}
