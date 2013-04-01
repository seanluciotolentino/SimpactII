package SimpactII.Agents;

import SimpactII.SimpactII;

/**
 *
 * @author Lucio Tolentino
 * 
 * The syphilisAgent is similar to the basic agent except it has the additional 
 * class variable "syphilisWeeksInfected". This allows the agent to be infected 
 * with syphilis as well as HIV. 
 * 
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
    
    /**
     *
     * @return the name of this agent.
     */
    @Override
    public String toString(){
        return "SyphilisAgent" + this.hashCode();
    }
    
    
    
    
}
