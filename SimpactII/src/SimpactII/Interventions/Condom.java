/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Interventions;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 *
 * @author Lucio Tolentino
 * 
 * An implementation of a week long condom distribution campaign
 * 
 */
public class Condom implements Intervention{
    
    //necessary class variables
    public double start;
    public double spend;
    public double condoms;
    
    //default class variables -- do not change here, change in your script
    public double condomInfectivityReduction = 0.80;
    public double condomsUsedPerWeek = 2;
    public int howMany = 10; //how many do you want / can you give to an individual at one time
    
    public Condom(){
        this(0,0);
    }
    
    public Condom( double startYear , double spend ) {
        this.start = startYear*52; //convert from year to weeks
        this.spend = spend;
    }

    @Override
    public void step(SimState s) { 
        //System.out.println(" ========CONDOM INTERVENTION " + s.schedule.getTime());
        this.condoms = costCurve(spend);
        SimpactII state = (SimpactII) s;
        state.addAttribute("isCondomUser", false);
        
        //for each condom, find a person and reduce their infectivity for a while
        int c = 0;
        while( c < condoms){
            final Agent target = findAgent(state);
            if(target == null){ break; }
            int howMany = howManyCondoms(target);
            if((boolean) target.attributes.get("isCondomUser")){
                c+=howMany; //this would be wastage I guess
                continue;   //don't reduce infectivity anymore
            }else{
                target.attributes.put("isCondomUser",true);
            }
                
            //change the TO and FROM infectivity
            double currentInfectivity = (double) target.attributes.get("infectivityChangeTo");
            target.attributes.put("infectivityChangeTo",currentInfectivity * (1-condomInfectivityReduction)) ;
            
            currentInfectivity = (double) target.attributes.get("infectivityChangeFrom");
            target.attributes.put("infectivityChangeFrom",currentInfectivity * (1-condomInfectivityReduction)) ;
            
            //schedule a step to change it back after the target has used all condoms
            double willRunOutIn = howMany / (condomsUsedPerWeek * target.partners); //partners actually changes but this is a good proxy I think
            state.schedule.scheduleOnceIn(willRunOutIn, new Steppable() {
                @Override
                public void step(SimState state) {
                    //revert infectivity
                    target.attributes.put("isCondomUser",false);
                    double currentInfectivity = (double) target.attributes.get("infectivityChangeTo");
                    target.attributes.put("infectivityChangeTo",currentInfectivity / (1-condomInfectivityReduction));
                    currentInfectivity = (double) target.attributes.get("infectivityChangeFrom");
                    target.attributes.put("infectivityChangeFrom",currentInfectivity / (1-condomInfectivityReduction));
                }
            });
            //
            c+=howMany;
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

    private double costCurve(double spend) {
        return (spend /0.05); //perhaps overly simplistic right now
    }
    
    protected Agent findAgent(SimpactII state){
        //this is a random targeting implementation
        //this may find same person twice -- for now this is okay and reflects
        //the reality in which it is sometimes difficult to recruit new individuals
        return (Agent) state.network.allNodes.get(state.random.nextInt(state.getPopulation() )); //note: must pull from network not myAgents
    }

    private int howManyCondoms(Agent target) {
        //this is a constant amount (how many can you give an individual at one time?
        return howMany; 
    }
    
}
