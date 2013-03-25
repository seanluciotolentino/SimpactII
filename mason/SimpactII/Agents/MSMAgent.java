/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class MSMAgent extends Agent{
    
    public MSMAgent(SimpactII s){
        super(s);
        this.male = true;
    }
    
    public boolean isDesirable(Agent other){
        return other.isLookingFor(this) && other.isMale();
    }
    
    public boolean isLookingFor(Agent other){
        return getPartners() < getDNP() && ( other.isMale() );
    }
    
    public String toString(){
        return "MSM" + this.hashCode();
    }
    
    
}
