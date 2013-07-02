/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.Interventions;

import SimpactII.Agents.*;
import SimpactII.Distributions.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import java.util.LinkedList;
import java.util.Queue;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class TestAndTreat implements Intervention {

    //parameters
    public String target;
    public double numSlots;
    public double numTests;
    public double retentionRate;
    
    //constants
    private final double start = 28.0 * 52;
    private final double numWeeks = 10.0 * 52; //from year 28 to 38
    private final double refuseTesting = 0.24;
    private final double timeTillNormalInfectivity = 0;
    private final double ARVInfectivityReduction = 0.96;
    private final double costOfTreatment = 500/52.0; //cost of ART per week
    private final double costOfTest = 1.0;
    
    //class variables
    public Bag patientsOnTreatment = new Bag();
    public Queue<Agent> treatmentQueue = new LinkedList();
    public Bag targets;
    public Distribution dropoutTimes ;
    public double treatmentTime = 0;
    public SimpactII state;

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
        this.dropoutTimes = new ExponentialDecay(52, retentionRate, state.random);
        SimpactII s = (SimpactII) state;
        this.state = s;

        //schedule test and treat to happen more frequently
        s.schedule.scheduleRepeating(new Steppable() {
            @Override
            public void step(SimState state) {
                SimpactII s = (SimpactII) state;
                testStep(s);
                treatStep(s);
            }
        },4);
    } //end step method

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {        
        return treatmentTime*costOfTreatment + (costOfTest * numTests * (numWeeks/4)); //???
    }

    /**
     * Test <b>numTests</b> individuals. If they are positive, add them to the
     * treatment queue.
     *
     */
    public void testStep(SimpactII s) {
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

    public void treatStep(SimpactII s) {
        //go through patients on treatment and remove anyone who has been removed
        for (int i = 0; i < patientsOnTreatment.size(); i++) { 
            Agent agent = (Agent) patientsOnTreatment.get(i);
            if(agent.timeOfRemoval<= s.schedule.getTime() )
                stopTreatment(agent);
        }
        
        //add patients from queue 
        while (patientsOnTreatment.size() < numSlots && !treatmentQueue.isEmpty()) {
            Agent patient = treatmentQueue.remove();
            if(patient.timeOfRemoval>= s.schedule.getTime() ) //check patient is still alive
                treat(patient, s);
        }
    }

    private void treat(final Agent patient, SimpactII state) {  
        patientsOnTreatment.add(patient);
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
                stopTreatment(patient);
            }
        });
        
        //push AIDS death back by timeOfDropOut
        Double AIDSDeath = (Double) patient.attributes.get("AIDSDeath");
        AIDSDeath += timeOfDropOut;
        patient.attributes.put("AIDSDeath",AIDSDeath);//I don't think this is necessary
            
        //for purposes of calculating cost:
        double timeLeft = (state.numberOfYears*52) - state.schedule.getTime();
        treatmentTime += Math.min(timeOfDropOut,timeLeft); //add the amount of time on ART for cost purposes
    }
    
    private void stopTreatment(Agent patient){
        double currentInfectivity = (double) patient.attributes.get("infectivityChangeFrom");
        patient.attributes.put("infectivityChangeFrom", currentInfectivity / (1 - ARVInfectivityReduction));
        patient.attributes.put("ARVStop", state.schedule.getTime());
        patientsOnTreatment.remove(patient);
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
