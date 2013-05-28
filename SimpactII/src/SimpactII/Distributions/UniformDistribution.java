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
public class UniformDistribution implements Distribution {

    private MersenneTwisterFast mtf;
    private double top;
    private double bottom;

    public UniformDistribution(double bottom, double top) {
        this.mtf = new MersenneTwisterFast();
        this.top = top;
        this.bottom = bottom;
    }

    public UniformDistribution(double bottom, double top, long seed) {
        this.mtf = new MersenneTwisterFast(seed);
        this.top = top;
        this.bottom = bottom;
    }

    public double nextValue() {
        return bottom + (mtf.nextDouble() * (top - bottom));
    }
}
