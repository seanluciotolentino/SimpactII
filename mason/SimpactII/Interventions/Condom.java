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
 * @author visiting_researcher
 */
public class Condom implements Intervention{
    
    double start;
    double spend;
    double condoms;
    double condomInfectivityReduction = 0.9999;
    
    public Condom( double startYear , double spend ) {
        this.start = startYear*52; //convert from year to weeks
        this.spend = spend;
        this.condoms = costCurve(spend);
    }

    @Override
    public void step(SimState s) {
        SimpactII state = (SimpactII) s;
        System.out.println("----------------------------------CONDOM INTERVENTION-------------------------------------");
        System.out.println("time = " + state.schedule.getTime());
        System.out.println("num condoms = " + condoms);
        System.out.println("will reach = " + condoms/20 );
        state.addAttribute("isCondomUser", false);
        
        //for each condom, find a person and reduce their infectivity for a while
        int c = 0;
        while( c < condoms){
            final Agent target = findAgent(state);
            int howMany = howManyCondoms(target);
            if((boolean) target.attributes.get("isCondomUser")){
                c+=howMany; //this would be wastage I guess
                continue;   //don't reduce infectivity anymore
            }else{
                target.attributes.put("isCondomUser",true);
            }
                
            
            double currentInfectivity = (double) target.attributes.get("infectivityChange");
            target.attributes.put("infectivityChange",currentInfectivity * (1-condomInfectivityReduction)) ;
            
            //System.out.println(c + "  target: " + target + " has infectivityChange reduced to " + target.attributes.get("infectivityChange"));
            
            //schedule a step to change it back after the target has used all condoms
            double now = state.schedule.getTime();
            
            state.schedule.scheduleOnce(now + howMany, new Steppable() {
                @Override
                public void step(SimState state) {
                    double currentInfectivity = (double) target.attributes.get("infectivityChange");
                    target.attributes.put("infectivityChange",currentInfectivity / (1-condomInfectivityReduction));
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
    
    private Agent findAgent(SimpactII state){
        //this is a random targeting implementation
        //this may find same person twice -- for now this is okay and reflects
        //the reality in which it is sometimes difficult to recruit new individuals
        return (Agent) state.network.allNodes.get(state.random.nextInt(state.getPopulation() )); //note: must pull from network not myAgents
    }

    private int howManyCondoms(Agent target) {
        //this is a constant amount (how many can you give an individual at one time?
        return 20; 
    }
    
}
