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
public class ExponentialDecay implements Distribution{
    
    private double X0;
    private double lambda;
    private MersenneTwisterFast r;
    
    public ExponentialDecay(double X0, double lambda, MersenneTwisterFast r ){
        this.X0 = X0;
        this.lambda = lambda;
        this.r = r;
    }
    
    public double nextValue(){
        //return X0*dist.nextDouble();
        return X0*(- Math.log(r.nextDouble()) / lambda);
    }
}
