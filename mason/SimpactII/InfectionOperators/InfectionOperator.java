/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 */
public class InfectionOperator implements Steppable{
    
    //default class variables about infection
    public int initialNumberInfected = 5;
    public int weeksStage1 = 12; //number of weeks in Primary Infection phase
    public double infectivityStage1 = 0.032;
    public int weeksStage2 = 4*12*8; //4 weeks / month x 12 months / year x 8 years = 384 weeks
    public double infectivityStage2 = 0.0035;
    public int weeksStage3 = 12; 
    public double infectivityStage3 = 0.0152;
    
    public InfectionOperator(SimpactII s){
        //perform initial infections
        for(int i = 0; i < initialNumberInfected; i++){
            Agent agent = (Agent) s.myAgents.get(s.random.nextInt(s.population));
            agent.weeksInfected = 1;
        }
    }
    
    public InfectionOperator(int[] weekStages, double[] infectivityStages, 
            int initialNumberInfected,SimpactII s){
        //grab infection parameters:
        this.weeksStage1 = weeksStage1; //number of weeks in Primary Infection phase
        this.weeksStage2 = weeksStage2;
        this.weeksStage3 = weeksStage3;
        
        this.infectivityStage1 = infectivityStage1;
        this.infectivityStage2 = infectivityStage2;
        this.infectivityStage3 = infectivityStage3;
        
        //perform initial infections
        this.initialNumberInfected = initialNumberInfected;
        for(int i = 0; i < initialNumberInfected; i++){
            Agent agent = (Agent) s.myAgents.get(s.random.nextInt(s.population));
            agent.weeksInfected = 1;
        }
    }
        
    public void step(SimState sim){
        SimpactII state = (SimpactII) sim;
        
        //flip coin for possible infections
        Bag agents = state.network.getAllNodes();
        for(int i = 0 ; i < agents.size(); i++){
            Agent agent = (Agent) agents.get(i);
            
            infectionStep(agent,state);           

        } //for all agents        
    } //end step
    
    private double infectivity(Agent agent) {
        if (agent.weeksInfected < weeksStage1)
            return infectivityStage1;
        else if (agent.weeksInfected < weeksStage2)
            return infectivityStage2;
        else
            return infectivityStage3;        
    }

    private void infectionStep(Agent agent, SimpactII state) {
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
    
}
