/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.Interventions;

import SimpactII.Agents.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import java.util.LinkedList;
import java.util.Queue;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Distributions.Distribution;
import sim.util.Distributions.ExponentialDecay;

/**
 *
 * @author visiting_researcher
 */
public class TestAndTreat implements Intervention {

    //parameters
    private String target;
    private double numSlots;
    private double numTests;
    private double retentionRate;
    
    //constants
    private final double start = 2.0 * 52;
    private final double numWeeks = 8.0 * 52; //from year 2 to 10
    private final double refuseTesting = 0.24;
    private final double timeTillNormalInfectivity = 4;
    private final double ARVInfectivityReduction = 0.96;
    private final double costOfTreatment = 500/52.0; //cost of ART per week
    private final double costOfTest = 1.0;
    
    //class variables
    private int patientsOnTreatment = 0;
    private Queue<Agent> treatmentQueue = new LinkedList();
    private Bag targets;
    public Distribution dropoutTimes = new ExponentialDecay(52, retentionRate);
    public double treatmentTime = 0;

    public TestAndTreat(String target, double numSlots,
            double numTests, double retentionRate) {
        //set intervention parameters
        this.target = target;
        this.numSlots = numSlots;
        this.numTests = numTests;
        this.retentionRate = retentionRate;
    }

    @Override
    public void step(SimState state) {
        //this is the first step of the intervention
        SimpactII s = (SimpactII) state;

        //schedule test and treat to happen more frequently
        s.schedule.scheduleRepeating(new Steppable() {
            @Override
            public void step(SimState state) {
                SimpactII s = (SimpactII) state;
                testStep(s);
                treatStep(s);
            }
        });
    } //end step method

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {        
        return treatmentTime*costOfTreatment + (costOfTest * numTests * numWeeks); //???
    }

    /**
     * Test <b>numTests</b> individuals. If they are positive, add them to the
     * treatment queue.
     *
     */
    private void testStep(SimpactII s) {
        Agent agent = (Agent) findAgent(s);
        for (int i = 0; i < numTests && agent != null; i++) {
            //check that this agent is a target and hasn't been tested
            if (agent.attributes.get("HIVTest") == null) {
                if (agent.weeksInfected > 0) { //test for HIV positive
                    treatmentQueue.add(agent);
                    agent.attributes.put("HIVTest", true);
                }
            }
            agent = (Agent) findAgent(s);
        }
    } //end testStep method

    private void treatStep(SimState s) {
        while (patientsOnTreatment < numSlots && !treatmentQueue.isEmpty()) {
            Agent patient = treatmentQueue.remove();
            treat(patient, s);
        }
    }

    private void treat(final Agent patient, SimState state) {
        patient.attributes.put("ARVStart", state.schedule.getTime());

        //reduce their infectivity
        double currentInfectivity = (double) patient.attributes.get("infectivityChangeFrom");
        patient.attributes.put("infectivityChangeFrom", currentInfectivity * (1 - ARVInfectivityReduction));

        //pull from predefined distribution for dropout time and 
        //schedule their infectivity to increase sometime after that
        double timeOfDropOut = dropoutTimes.nextValue(); //transform exponential decay into CDF -- this should be more generic no???
        state.schedule.scheduleOnceIn(timeOfDropOut + timeTillNormalInfectivity, new Steppable() {
            @Override
            public void step(SimState state) {
                double currentInfectivity = (double) patient.attributes.get("infectivityChangeFrom");
                patient.attributes.put("infectivityChangeFrom", currentInfectivity / (1 - ARVInfectivityReduction));
                patient.attributes.put("ARVStop", state.schedule.getTime());
                patientsOnTreatment--;
            }
        });
        
        treatmentTime += Math.min(timeOfDropOut,numWeeks); //add the amount of time on ART for cost purposes
    }

    protected Agent findAgent(SimpactII state) {
        Bag myAgents = new Bag(state.network.getAllNodes());
        myAgents.shuffle(state.random);
        int numAgents = myAgents.size();
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) myAgents.get(i);
            if (isTarget(agent)) {
                return agent;
            }
        }
        return null;
    }

    private boolean isTarget(Agent agent) {
        switch (target) {
            case "generalPopulation":
                return true;
            case "msm":
                return agent.getClass() == MSMAgent.class;
            case "sexWorker":
                return agent.getClass() == SexWorkerAgent.class;
            case "young":
                return agent.getAge() <= 25;
            case "highRisk":
                return agent.partners > 1;
            default:
                throw new RuntimeException("Unknown target group: " + target);
        }
    }
}
