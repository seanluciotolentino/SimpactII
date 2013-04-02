package SimpactII.InfectionOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * Default InfectionOperator. This operator controls how HIV infection spreads 
 * through the network.  There are four constructors so that transmission probability
 * and initial number infected can vary based on model structure. At each step,
 * the operator goes through every node and calls the "infectionStep" method which 
 * in turn relies on the "infectivity" method. Both can be overwritten for 
 * customization. 
 * 
 */
public class InfectionOperator implements Steppable{
    
    //default class variables about infection
    public double transmissionProbability = 0.01;
    public int initialNumberInfected = 5;
    
    public InfectionOperator(){
    }
    
    public InfectionOperator(int initialNumberInfected){
        this.initialNumberInfected = initialNumberInfected;
    }
    
    public InfectionOperator(double infectivity){
        this.transmissionProbability = infectivity;
    }
    
    public InfectionOperator(int initialNumberInfected, double infectivity){
        this.initialNumberInfected = initialNumberInfected;
        this.transmissionProbability = infectivity;
    }
        
    public final void step(SimState sim){
        SimpactII state = (SimpactII) sim;
        
        //flip coin for possible infections
        Bag agents = state.network.getAllNodes();
        for(int i = 0 ; i < agents.size(); i++){
            Agent agent = (Agent) agents.get(i);
            
            infectionStep(agent,state);           

        } //for all agents        
    } //end step
    
    public double infectivity(Agent agent) {
        return transmissionProbability;       
    }

    public void infectionStep(Agent agent, SimpactII state) {
        //find those that are actually infected
        if (agent.weeksInfected>= 1){ 
            agent.setWeeksInfected(agent.getWeeksInfected() + 1);

            Bag partners = state.network.getEdges(agent,new Bag());
            for(int j = 0 ; j < partners.size(); j++){
                Edge relationship = (Edge) partners.get(j);
                Agent partner = (Agent) relationship.getOtherNode(agent);

                if (partner.weeksInfected<=0 && state.random.nextDouble() < infectivity(agent) ){
                    partner.setInfector(agent);
                    partner.weeksInfected = 1;
                }
            } //partners for loop
        } //if agent infected
    }
    
    public void performInitialInfections(SimpactII state){
        int pop = state.getPopulation();
        if (pop < initialNumberInfected){
            System.err.println("Population too small for " + initialNumberInfected + " individuals to be initially infected." + 
                    " Decrease initial number infected or increase population size");
            return;
        }
        for(int i = 0; i < initialNumberInfected; i++){
            Agent agent = (Agent) state.myAgents.get(state.random.nextInt(pop));
            agent.weeksInfected = 1;
        }
    }
    
    
}
