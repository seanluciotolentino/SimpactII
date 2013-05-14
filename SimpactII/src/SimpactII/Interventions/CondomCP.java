package SimpactII.Interventions;

import SimpactII.Agents.*;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * The Condom distribution campaign used in the combination prevention paper.
 * 
 */
public class CondomCP implements Intervention {
    
    //settable parameters
    private String target;
    private int condomsPerInterval;
    private int interval;
    
    //parameters that are set for the combination prevention paper
    public double start = 2.0*52;
    private double weeklyEmployeeSalary = 150;//$150 ~ R1250, the weekly income of an NGO employee
    private double numWeeks = 8.0 * 52; //from year 2 to 10
    private double costOfCondom = 0.05;
    public int howMany = 10;
    private double condomsUsedPerWeek = 2; //2 sex acts
    private double condomInfectivityReduction = 0.80;
    
    public CondomCP(String target, double condoms, double interval){
        this.target = target;
        this.condomsPerInterval = (int) condoms;
        this.interval = (int) interval;
    }

    @Override
    public void step(SimState s) {
        SimpactII state = (SimpactII) s;
        
        //for each condom, find a person and reduce their infectivity for a while
        int c = 0;
        while( c < condomsPerInterval){
            final Agent target = findAgent(state);
            if(target == null){ break; }
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
            double partners = Math.max(1, target.partners);
            double willRunOutIn = howMany / (condomsUsedPerWeek * partners); //partners actually changes but this is a good proxy I think
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
        }//end while
        s.schedule.scheduleOnceIn(interval, this);
    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {
        double cost = weeklyEmployeeSalary; //let's say it takes a week to scout locations
        cost += (numWeeks/interval)*condomsPerInterval*costOfCondom;     //cost of condoms
        cost += weeklyEmployeeSalary *      //cost of employeeing someone full time to do this
                (Math.pow(0.5,Math.log(interval)/Math.log(2))); 
        
        return cost; 
    }
    
    protected Agent findAgent(SimpactII state){
        //goes through alive agents and finds target group. Note:
        //  -can be computationally expensive
        //  -doesn't account for difficulty in finding individuals
        
        Bag myAgents = new Bag(state.network.getAllNodes());
        myAgents.shuffle(state.random); 
        int numAgents = myAgents.size();
        for (int i = 0; i < numAgents; i++) {
            Agent agent = (Agent) myAgents.get(i);
            if( isTarget(agent) )
                return agent; 
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
                    return false; //this shouldn't happen but Java requires the statement
            }
    }
    
}
