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

    private MersenneTwisterFast r;
    private double top;
    private double bottom;

    public UniformDistribution(double bottom, double top, MersenneTwisterFast r) {
        this.r = r;
        this.top = top;
        this.bottom = bottom;
    }

    public double nextValue() {
        return bottom + (r.nextDouble() * (top - bottom));
    }
}
