/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.Distributions.*;
import SimpactII.SimpactII;
import java.util.HashMap;

/**
 *
 * @author visiting_researcher
 */
public class SugarDaddyAgent extends Agent{
    
    Distribution swRelationshipDistribution = new UniformDistribution(1.0, 2.0);
    double MIN_AGE = 40;
    double MAX_AGE = 65;
    
    public SugarDaddyAgent(SimpactII state, HashMap attributes){
        super(state,attributes);
        this.male = true;
        this.age = (state.random.nextDouble()*(MAX_AGE - MIN_AGE)) + MIN_AGE; //random age between MIN and MAX
    }
    
    public boolean isLookingFor(Agent other){
        //other.isSeeking: other agent must seeking more partners and not age conscious
        return other.getAge() < 25 && other.isSeeking(this); 
    }
    
    public Agent replace(SimpactII state){
        return new SugarDaddyAgent(state,attributes);
    }
    
}
