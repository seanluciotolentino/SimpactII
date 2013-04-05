/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Interventions;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class MaleCircumcision implements Intervention{
    
    //necessary class variables
    public double start;
    public double spend;
    public double circumcisionsPerformed;
    
    //default class variables
    public double circumcisionInfectivityReduction = 0.65;
    public double circumcisionCost = 50; //one circumcision costs about 50 USD
    
    public MaleCircumcision( double startYear , double spend ) {
        this.start = startYear*52; //convert from year to weeks
        this.spend = spend;
        this.circumcisionsPerformed = spend / circumcisionCost;
    }

    @Override
    public void step(SimState s) {
        SimpactII state = (SimpactII) s;
        
        //find males
        state.addAttribute("circumcised", false);
        Bag uncircumcisedMales = findAgents(state);
        int circumcisionsToPerform = (int) Math.min(uncircumcisedMales.size() , circumcisionsPerformed);
        
        //System.out.println("-----------------------MCC " + state.schedule.getTime() + "----");
        //System.out.println("number to treat = " + circumcisionsToPerform);
        
        //circumcise those that you can
        for(int i = 0; i < circumcisionsToPerform ; i++){
            Agent target = (Agent) uncircumcisedMales.get(i);
            
            //reduce their infectivity
            double currentInfectivity = (double) target.attributes.get("infectivityChangeTo");
            target.attributes.put("infectivityChangeTo",currentInfectivity * (1-circumcisionInfectivityReduction)) ;     
            //System.out.println("Circumcising " + target.hashCode() );
        }
    }

    @Override
    public double getStart() {
        return this.start;
    }

    @Override
    public double getSpend() {
        return this.spend;
    }

    private Bag findAgents(SimpactII state) {
        //I assume it's not too difficult to find young men... admittedly this is a weak assumption
        Bag agents = state.network.getAllNodes();
        Bag uncircumcisedMales = new Bag();
        int numAgents = agents.size();
        for(int i = 0; i < numAgents; i++){
            Agent agent = (Agent) agents.get(i);
            if ( agent.isMale() )
                uncircumcisedMales.add( agent ); 
        }
        return uncircumcisedMales;
    }
    
}
