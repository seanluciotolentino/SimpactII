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
public class SyphilisAgent extends Agent{
    
    public int syphilisWeeksInfected = 0;
    
    public SyphilisAgent(SimpactII state){
        super(state);
        //same as other agents, just have additional class variable
        //to denote infection with syphilis
        
    }

    /**
     * @return the syphilisWeeksInfected
     */
    public int getSyphilisWeeksInfected() {
        return syphilisWeeksInfected;
    }
    
    public String toString(){
        return "SyphilisAgent" + this.hashCode();
    }
    
    
    
    
}
