/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Distributions;

import ec.util.MersenneTwisterFast;

/**
 *
 * @author visiting_researcher
 */
public class PowerLawDistribution implements Distribution{
    
    public double alpha;
    public double cut;
    private MersenneTwisterFast r;
    
    public PowerLawDistribution(double alpha, double cut, MersenneTwisterFast r){
        this.alpha = alpha;
        this.r = r;
    }

    public double nextValue() {
        //return sim.util.distribution.Distributions.nextPowLaw(alpha, cut, mtf);
        //return Math.pow(r.nextDouble() , alpha);
        return cut*Math.pow(r.nextDouble(), 1.0/(alpha+1.0) ) ;
    }
    
    
    
}
