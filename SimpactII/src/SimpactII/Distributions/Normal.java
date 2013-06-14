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
public class Normal implements Distribution{
    
    private final double mean;
    private final double sd;
    private final MersenneTwisterFast r;
    protected double cache; // cache for Box-Mueller algorithm 
    protected boolean cacheFilled = false; // Box-Mueller
    
    
    public Normal(double mean, double sd, MersenneTwisterFast r){
        this.mean = mean;
        this.sd = sd;
        this.r = r;
    }
    
    public double nextValue() {
        // Uses polar Box-Muller transformation.
        if (cacheFilled) {
            cacheFilled = false;
            return cache; 
            };

        double x,y,w,z;
        do {
            x = 2.0*r.nextDouble() - 1.0; 
            y = 2.0*r.nextDouble() - 1.0;              
            w = x*x+y*y;
            } while (w >= 1.0);

        z = Math.sqrt(-2.0*Math.log(w)/w);
        cache = mean + sd*x*z;
        cacheFilled = true;
        return mean + sd*y*z;
    }
    
    public static void main(String[] args){
        Normal n = new Normal(1000,250, new MersenneTwisterFast());
        System.out.print("hist([");
        for(int i =0; i < 500; i++)
            System.out.print(Math.round(n.nextValue()) + ",");
        System.out.println("],100:50:2000)");
    }
    
}
