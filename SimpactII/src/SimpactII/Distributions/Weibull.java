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
public class Weibull implements Distribution{
    private final double scale;
    private final double shape;
    private final MersenneTwisterFast r;
    
    public Weibull(double scale, double shape, MersenneTwisterFast r){
        this.scale = scale;
        this.shape = shape;
        this.r = r;
    }

    @Override
    public double nextValue() {
        return scale * Math.pow(-Math.log(1.0 - r.nextDouble()), 1.0 / shape);
    }
    
}
