package CombinationPrevention.Interventions;

import SimpactII.Agents.*;
import SimpactII.Interventions.Intervention;
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
public class Condom implements Intervention {
    
    //settable parameters
    private String target;
    public int condomsPerInterval;
    private int interval;
    
    //parameters that are set for the combination prevention paper
    private final double start = 28.0*52;
    private final double numWeeks = 10.0 * 52; //from year 28 to 38
    private final double weeklyEmployeeSalary = 150;//$150 ~ R1250, the weekly income of an NGO employee
    private final double costOfCondom = 0.05;
    private final int howMany = 10;
    private final double condomsUsedPerWeek = 2; //2 sex acts
    private final double condomInfectivityReduction = 0.80;
    
    public Condom(String target, double condomsPerInterval, double interval){
        this.target = target;
        this.condomsPerInterval = (int) condomsPerInterval;
        this.interval = (int) interval;//someone might try to give you an interval of 0
    }

    @Override
    public void step(SimState state) {
        if(interval<=0){return;} //intervention doesn't happen if interval is 0
        SimpactII s = (SimpactII) state;
        s.addAttribute("isCondomUser", false);
        
        s.schedule.scheduleRepeating(new Steppable(){
            @Override
            public void step(SimState state) {
                distributeCondoms((SimpactII) state);
            }            
        }, interval);
        

    }
    
    public void distributeCondoms(SimpactII state){
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
    }

    @Override
    public double getStart() {
        return start;
    }

    @Override
    public double getSpend() {
        if(interval<=0){return 0.0;} //intervention doesn't happen if interval is 0
        double cost = weeklyEmployeeSalary; //let's say it takes a week to scout locations
        cost += (numWeeks/interval)*condomsPerInterval*costOfCondom;     //cost of condoms
        cost += (numWeeks/interval)*weeklyEmployeeSalary ;      //cost of employeeing someone full time to do this
                //(Math.pow(0.5,Math.log(interval)/Math.log(2))); 
        
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
