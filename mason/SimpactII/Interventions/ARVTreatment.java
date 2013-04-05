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
import sim.util.Distributions.Distribution;
import sim.util.Distributions.ExponentialDecay;

/**
 *
 * @author visiting_researcher
 */
public class ARVTreatment implements Intervention{
    
    //intervention variables
    public double start;
    public double spend;
    
    //ARV variables-- do not change here, change in your script
    public double ARVInfectivityReduction = 0.96;
    public Distribution retentionRate = new ExponentialDecay(1,0.1/52);
    public double costPerARV = 500; 
    public double timeTillNormalInfectivity = 4; //4 weeks -- a guess from Wim
    public double numberOfARVs;
        
    //main constructor
    public ARVTreatment(double startYear, double spend){
        this.start = startYear*52; //convert from year to weeks
        this.spend = spend;
        this.numberOfARVs = spend / costPerARV;
    }

    @Override
    public void step(SimState s) {
        SimpactII state = (SimpactII) s;
        state.addAttribute("onTreatment", false);
        
        //find all the positives and treat them
        Bag positives = findAgents(state);
        int numberToTreat = (int) Math.min(positives.size() , numberOfARVs);
        
        //System.out.println("-----------------------ARV Treatment " + state.schedule.getTime() + "----");
        //System.out.println("number to treat = " + numberToTreat);
        //System.out.println("time till normal infectivity = " + timeTillNormalInfectivity);
        
        for(int i = 0; i < numberToTreat ; i++){
            final Agent target = (Agent) positives.get(i);
            
            //System.out.println("Treating " + target.hashCode() );
            
            //reduce their infectivity
            double currentInfectivity = (double) target.attributes.get("infectivityChangeFrom");
            target.attributes.put("infectivityChangeFrom",currentInfectivity * (1-ARVInfectivityReduction)) ;     
            
            //pull from predefined distribution for dropout time and 
            //schedule their infectivity to increase sometime after that
            double timeOfDropOut = retentionRate.nextValue(); //transform exponential decay into CDF -- this should be more generic no???
            state.schedule.scheduleOnceIn(timeOfDropOut + timeTillNormalInfectivity, new Steppable() {
                @Override
                public void step(SimState state) {
                    System.out.println("Agent " + target.hashCode() + " is leaving treatment at " + state.schedule.getTime());
                    double currentInfectivity = (double) target.attributes.get("infectivityChangeFrom");
                    target.attributes.put("infectivityChangeFrom",currentInfectivity / (1-ARVInfectivityReduction));
                }
            });
        }
    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {
        return spend;
    }
    
    private Bag findAgents(SimpactII state){
        //I assume that if they were tested we are able to easily contact them
        //for followup of treatment
        Bag agents = state.network.getAllNodes();
        Bag positives = new Bag();
        int numAgents = agents.size();
        for(int i = 0; i < numAgents; i++){
            Agent agent = (Agent) agents.get(i);
            if ((Boolean) agent.attributes.get("HIVTest"))
                positives.add( agent ); 
        }
        if(positives.size() <= 0)
            System.err.println("No HIV positive individuals found: Note that HIVTestAndCounsel intervention must be performed first");
        return positives;
    }

    private Boolean test(Agent agent) {
        return agent.weeksInfected>0;
    }
}