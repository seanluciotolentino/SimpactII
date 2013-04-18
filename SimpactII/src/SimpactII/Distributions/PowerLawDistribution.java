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
    
    public PowerLawDistribution(double alpha){
        this.mtf = new MersenneTwisterFast();
        this.alpha = alpha;
        //this.cut = cut;               
    }
    
    public PowerLawDistribution(double alpha, long seed){
        this.mtf = new MersenneTwisterFast(seed);
        this.alpha = 1 / alpha;
        //this.cut = cut;               
    }

    public double nextValue() {
        //return sim.util.distribution.Distributions.nextPowLaw(alpha, cut, mtf);
        return Math.pow(mtf.nextDouble() , alpha);
    }
    
    
    
}
