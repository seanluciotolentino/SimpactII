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
    private MersenneTwisterFast mtf;
    
    public ExponentialDecay(double X0, double lambda ){
        this.X0 = X0;
        this.lambda = lambda;
    }
    
    public double nextValue(){
        //return X0*dist.nextDouble();
        return X0*(- Math.log(mtf.nextDouble()) / lambda);
    }
    
    public static void main(String[] args){
        ExponentialDecay ed = new ExponentialDecay(1, 0.1/52);
        for(int i = 0; i < 100 ; i++){
            System.out.print(ed.nextValue() + ", ");
        }
    }
    
}
