package CombinationPrevention.Interventions;

import SimpactII.Agents.Agent;
import SimpactII.Agents.MSMAgent;
import SimpactII.Agents.SexWorkerAgent;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class MaleCircumcision implements Intervention{
    //settable parameters
    private String target;
    private double circumcisions;
    private double recruitment;
    
    //parameters that are constant for CP paper
    public double start = 2.0*52;
    public double circumcisionInfectivityReduction = 0.6;
    private double costPerCircumcision = 50;
    
    
    public MaleCircumcision(String target, double circumcisions, double recruitment){
        this.target = target;
        this.circumcisions = circumcisions;
        this.recruitment = recruitment;
    }
    
    @Override
    public void step(SimState s) {
        SimpactII state = (SimpactII) s;
        
        //find males
        state.addAttribute("circumcised", false);
        Bag uncircumcisedMales = findAgents(state);
        int circumcisionsToPerform = (int) Math.min(uncircumcisedMales.size() , circumcisions);
        
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
        return start;
    }

    @Override
    public double getSpend() {
        return circumcisions * costPerCircumcision;
    }
    
    protected Bag findAgents(SimpactII state) {
        //I assume it's not too difficult to find young men... admittedly this is a weak assumption
        Bag agents = state.network.getAllNodes();
        Bag uncircumcisedMales = new Bag();
        int numAgents = agents.size();
        for(int i = 0; i < numAgents; i++){
            Agent agent = (Agent) agents.get(i);
            if ( agent.isMale() && isTarget(agent) )
                uncircumcisedMales.add( agent ); 
        }
        return uncircumcisedMales;
    }

    private boolean isTarget(Agent agent) {
            switch (target) {
                case "generalPopulation":
                    return true;
                case "msm":
                    return agent.getClass() == MSMAgent.class;
                case "young":
                    return agent.getAge() <= 25;
                case "highRisk":
                    return agent.partners > 1;
                default:
                    return false; //this shouldn't happen but Java requires the statement
            }
    }


    
}
