/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.InfectionOperators;

import SimpactII.Agents.Agent;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class InterventionInfectionOperator extends InfectionOperator{
    
    public InfectionOperator parent;
    
    public InterventionInfectionOperator(){    
        this.parent = new InfectionOperator();
    }
    public InterventionInfectionOperator(InfectionOperator parent){
        this.parent = parent;
    }
        
    @Override
    public double infectivity(Agent agent) {
        return parent.infectivity(agent) * (double) agent.attributes.get("infectivityChange");
    }
    
    public void preProcess(SimpactII state){
        parent.preProcess(state);
        
        //add "infectivityChange" to agents attribute lists
        state.addAttribute("infectivityChange", 1.0);
                
        //schedule all the interventions to be executed
        int numInterventions = state.myInterventions.size();
        for (int i = 0; i < numInterventions ; i++){
            Intervention intervention = (Intervention) state.myInterventions.get(i);
            state.schedule.scheduleOnce(intervention.getStart(), intervention);
        }
    }
    
}
