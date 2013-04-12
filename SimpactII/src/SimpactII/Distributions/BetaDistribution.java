/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.util.Distributions;

import ec.util.MersenneTwisterFast;
import sim.util.distribution.Beta;

/**
 *
 * @author visiting_researcher
 */
public class BetaDistribution implements Distribution{
    
    private MersenneTwisterFast mtf;
    private Beta beta;
    
    public BetaDistribution(double alpha, double beta){
        this.mtf = new MersenneTwisterFast();
        this.beta = new Beta(alpha,beta,mtf);
    }
    
    public BetaDistribution(double alpha, double beta, long seed){
        this.mtf = new MersenneTwisterFast(seed);
        this.beta = new Beta(alpha,beta,mtf);     
    }

    public double nextValue() {
        return this.beta.nextDouble();
    }
    
}
