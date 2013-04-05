/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Interventions;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.engine.SimState;

/**
 *
 * @author Lucio Tolentino
 * 
 * An implementation of an week long HIV testing campaign
 * 
 */
public class HIVTestAndCounsel implements Intervention{
    
    //intervention variables
    public double start;
    public double spend;
    
    //HIVtest variables -- do not change here, change in your script
    public double costPerTest = 1; 
    public double numberOfTests;
    private int partnersAfterTest = 1;
    
    
    public HIVTestAndCounsel(double startYear, double spend){
        this.start = startYear*52; //convert from year to weeks
        this.spend = spend;
        this.numberOfTests = spend / costPerTest;
    }

    @Override
    public void step(SimState s) {
        SimpactII state = (SimpactII) s;
        state.addAttribute("HIVTest", false);
        //System.out.println("----------------------------HIV Test Campaign " + state.schedule.getTime() + "----");
        
        for(int i = 0; i < numberOfTests ; i++){
            Agent target = findAgent(state);
            target.attributes.put("HIVTest", test(target));
            //if positive, counsel them to have fewer partners, but don't reduce infectivity
            if( test(target) )
                target.DNP = partnersAfterTest; //note this might actually increase some individuals DNP
            //System.out.println("Tested Agent" + target.hashCode() + " (s)he was " + test(target));
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
    int agentCount = 0; //DEBUG
    private Agent findAgent(SimpactII state){
        //this is a random targeting implementation
        //this may find same person twice -- for now this is okay and reflects
        //the reality in which it is sometimes difficult to recruit new individuals
        //return (Agent) state.network.allNodes.get(state.random.nextInt(state.getPopulation() )); //note: must pull from network not myAgents
        agentCount = Math.min(agentCount, state.getPopulation() -1);
        return (Agent) state.network.allNodes.get(agentCount++ ); //DEBUG -- everyone is tested
    }
    
    private Boolean test(Agent agent) {
        return agent.weeksInfected>0;
    }
}
