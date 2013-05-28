/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Distributions;

import SimpactII.Distributions.distribution.Exponential;
import ec.util.MersenneTwisterFast;

/**
 *
 * @author visiting_researcher
 */
public class ExponentialDecay implements Distribution{
    
    private double X0;
    private double lambda;
    private Exponential dist;
    
    public ExponentialDecay(double X0, double lambda ){
        this.X0 = X0;
        this.lambda = lambda;
        this.dist = new Exponential(lambda, new MersenneTwisterFast() );
    }
    
    public double nextValue(){
        return X0*dist.nextDouble();
    }
    
    public static void main(String[] args){
        ExponentialDecay ed = new ExponentialDecay(1, 0.1/52);
        for(int i = 0; i < 100 ; i++){
            System.out.print(ed.nextValue() + ", ");
        }
    }
    
}
