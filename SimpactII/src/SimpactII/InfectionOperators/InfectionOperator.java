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
    public double transmissionProbability = 0.01; //this should reflect sex acts per week as well
    public int initialNumberInfected = 5;
    public double HIVIntroductionTime = 1;
    
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
        
    public final void step(SimState s){
        SimpactII state = (SimpactII) s;
        
        //flip coin for possible infections
        int numAgents = state.myAgents.size();
        for(int i = 0 ; i < numAgents; i++){
            Agent agent = (Agent) state.myAgents.get(i);
            
            infectionStep(agent,state);           

        } //for all agents        
    } //end step

    public void infectionStep(Agent agent, SimpactII state) {
        //find those that are actually infected
        if (agent.weeksInfected>= 1){ 
            agent.setWeeksInfected(agent.getWeeksInfected() + 1);

            Bag partners = state.network.getEdges(agent,new Bag());
            int numPartners = partners.size();
            for(int j = 0 ; j < numPartners; j++){
                Edge relationship = (Edge) partners.get(j);
                Agent partner = (Agent) relationship.getOtherNode(agent);

                if (partner.weeksInfected<=0 && state.random.nextDouble() < infectivity(agent,partner) )
                    infect(partner,agent);                
            } //partners for loop
        } //if agent infected
    }
    
    public void infect(Agent infected, Agent infector){
        infected.setInfector(infector);
        infected.weeksInfected = 1;
    }
    
    public double infectivity(Agent from, Agent to) {
        return transmissionProbability;  //default, it doesn't matter who the agents are     
    }
    
    public void preProcess(final SimpactII state){
        state.schedule.scheduleOnceIn(HIVIntroductionTime, 
            new Steppable() {
                @Override
                public void step(SimState ss) {
                    performInitialInfections(state);
                }
        });
    }
    
    public void performInitialInfections(SimpactII state){
        int pop = state.getPopulation();
        if (pop < initialNumberInfected){
            System.err.println("Population too small for " + initialNumberInfected + " individuals to be initially infected." + 
                    " Decrease initial number infected or increase population size");
            return;
        }
        for(int i = 0; i < initialNumberInfected; i++){
            Agent agent = (Agent) state.network.allNodes.get(state.random.nextInt(pop));
            agent.weeksInfected = 1;
        }
    }


    }
