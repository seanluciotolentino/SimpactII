package CombinationPrevention.Interventions;

import SimpactII.Agents.Agent;
import SimpactII.Agents.MSMAgent;
import SimpactII.Agents.SexWorkerAgent;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class MaleCircumcision implements Intervention {
    
    //parameters
    private String target;
    private double numCircumcisions;
    
    //constants
    private final double start = 20.0 * 52;
    private final double numWeeks = 8.0 * 52; //from year 2 to 10
    private final double circumcisionInfectivityReduction = 0.6;
    private final double costPerCircumcision = 50;
    
    //class variables
    

    public MaleCircumcision(String target, double numCircumcisions) {
        this.target = target;
        this.numCircumcisions = numCircumcisions;
    }

    @Override
    public void step(SimState state) {
        final SimpactII s = (SimpactII) state;
        s.addAttribute("circumcised", false);

        s.schedule.scheduleRepeating(new Steppable() {
            @Override
            public void step(SimState state) {
                //find males
                SimpactII s = (SimpactII) state;
                Bag uncircumcisedMales = findAgents(s);
                int circumcisionsToPerform = (int) Math.min(uncircumcisedMales.size(), numCircumcisions);

                //circumcise those that you can
                for (int i = 0; i < circumcisionsToPerform; i++) {
                    Agent target = (Agent) uncircumcisedMales.get(i);

                    //reduce their infectivity
                    double currentInfectivity = (double) target.attributes.get("infectivityChangeTo");
                    target.attributes.put("infectivityChangeTo", currentInfectivity * (1 - circumcisionInfectivityReduction));
                }
            }
        });


    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {
        return numCircumcisions * costPerCircumcision * numWeeks;
    }

    protected Bag findAgents(SimpactII state) {
        //I assume it's not too difficult to find young men... admittedly this is a weak assumption
        Bag agents = state.network.getAllNodes();
        Bag uncircumcisedMales = new Bag();
        int numAgents = agents.size();
        for (int i = 0; i < numAgents && uncircumcisedMales.size() >= numCircumcisions; i++) {
            Agent agent = (Agent) agents.get(i);
            if (agent.isMale() && isTarget(agent))
                uncircumcisedMales.add(agent);            
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
