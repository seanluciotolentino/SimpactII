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
    public double infectivity(Agent from, Agent to) {
        //check the parent infectivity by the amount in the attributes of the
        //"from" agent and the "to" agent. Note that this is somewhat naive as
        //both individuals using condoms will not twice reduce the infectivity,
        //but it should be reduced twice if a female is on ARV and a male is
        //circumcised.
        return parent.infectivity(from,to) * (double) from.attributes.get("infectivityChangeFrom")
                * (double) to.attributes.get("infectivityChangeTo");
    }
    
    public void preProcess(SimpactII state){
        parent.preProcess(state);
        
        //add "infectivityChange" to agents attribute lists
        state.addAttribute("infectivityChangeFrom", 1.0);
        state.addAttribute("infectivityChangeTo", 1.0);
                
        //schedule all the interventions to be executed
        int numInterventions = state.myInterventions.size();
        for (int i = 0; i < numInterventions ; i++){
            Intervention intervention = (Intervention) state.myInterventions.get(i);
            state.schedule.scheduleOnce(intervention.getStart(), intervention);
        }
    }
    
}
