package SimpactII.Interventions;

import SimpactII.Agents.*;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * The Condom distribution campaign used in the combination prevention paper.
 * 
 */
public class CondomCP extends Condom {
    
    //settable parameters
    private String target;
    private int condomsPerInterval;
    private int interval;
    
    //parameters that are set for the combination prevention paper
    public double start = 2.0*52;
    private double weeklyEmployeeSalary = 150;//$150 ~ R1250, the weekly income of an NGO employee
    private double numWeeks = 8.0 * 52; //from year 2 to 10
    private double costOfCondom = 0.05;
    
    public CondomCP(String target, int condoms, int interval){
        this.target = target;
        this.condomsPerInterval = condoms;
        this.interval = interval;
    }

    @Override
    public void step(SimState s) {
        //System.out.println("===========" + s.schedule.getTime() + "=======");
        this.spend = condomsPerInterval*costOfCondom;
        super.step(s);
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
