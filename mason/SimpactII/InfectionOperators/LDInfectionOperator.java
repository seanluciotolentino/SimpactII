/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.InfectionOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;

/**
 *
 * @author Lucio Tolentino
 * 
 * The "LD" stands for "Linear Decay".  This is an example of how to replace the 
 * default InfectionOperator with a slightly different one.  This operator will 
 * extend (or inherit) from the base InfectionOperator the step method, but will 
 * replace a constructor and the infectivity method.
 * 
 */
public class LDInfectionOperator extends InfectionOperator{
    
    public double infectivityGrowth = 0.002;
        
    public LDInfectionOperator(SimpactII s){
        super(s);
    }
    
    public double infectivity(Agent agent){
        return agent.weeksInfected*infectivityGrowth;
    }
    
    
    
}
