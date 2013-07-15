/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SleepOp.Operators;

import SimpactII.Agents.Agent;
import SimpactII.InfectionOperators.InfectionOperator;
import SimpactII.SimpactII;
import SleepOp.Agents.SleepingAgentInterface;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio
 */
public class SleepingInfectionOperator extends InfectionOperator{
    
    protected SimpactII state;
    
    public void preProcess(SimpactII state){
        this.state = state;
        super.preProcess(state);
    }
    
    /*
     * After performing the initial infections, put "non-important" agents to 
     * sleep. Infected, and partners of infected are left awake.
     */
    public void performInitialInfections(SimpactII s){
        super.performInitialInfections(s);
        
        //after initial infections --> sleep some agents 
        Bag agents = s.network.getAllNodes();
        for (int i = 0; i < agents.size(); i++) { 
            SleepingAgentInterface sagent = (SleepingAgentInterface) agents.get(i);            
            Agent agent = (Agent) sagent;
            
            //set everyone to sleep...
            sagent.setSleeping(true);
            
            //...unless infected...
            if(agent.weeksInfected >= 1)
                sagent.setSleeping(false);
            
            //...or partner is infected.
            Bag partners = s.network.getEdges(agent,new Bag());
            int numPartners = partners.size();
            for(int j = 0 ; j < numPartners; j++){
                Edge relationship = (Edge) partners.get(j);
                Agent partner = (Agent) relationship.getOtherNode(agent);
                if(partner.weeksInfected>=1)
                    sagent.setSleeping(false);
            } //partners for loop
            
        }//for loop
    }
    
    /*
    * After performing an infection, "wake up" the individual         * 
    */
    public void infect(Agent infected, Agent infector){
        super.infect(infected,infector);
        
        //wake up infected
        ((SleepingAgentInterface) infected).setSleeping(false);
        
        //and wake up his/her partners
        Bag partners = state.network.getEdges(infected,new Bag());
        int numPartners = partners.size();
        for(int j = 0 ; j < numPartners; j++){
            Edge relationship = (Edge) partners.get(j);
            SleepingAgentInterface partner = (SleepingAgentInterface) relationship.getOtherNode(infected);
            partner.setSleeping(false);
        }
        
    }
    
    
}
