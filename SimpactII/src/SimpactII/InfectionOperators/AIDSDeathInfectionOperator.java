/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.InfectionOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import SimpactII.Distributions.*;

/**
 *
 * @author visiting_researcher
 */
public class AIDSDeathInfectionOperator extends InfectionOperator{
    
    private SimpactII state;
    
    public void preProcess(SimpactII state){
        super.preProcess(state);
        
        //give AIDS death time to the intially infected
        //this may be better moved to "performInitialInfections"
//        int numAgents = state.myAgents.size();
//        for(int i = 0 ; i < numAgents; i++){
//            Agent agent = (Agent) state.myAgents.get(i);
//            if(agent.weeksInfected>0){
//                double timeTillDeath = 52*8;
//                agent.attributes.put("AIDSDeath", 
//                    new Double(timeTillDeath+state.schedule.getTime()));
//            }
//        }
        this.state = state;      
    }
    
    public void performInitialInfections(SimpactII state){
        //perform initial infections in 25-35
        int pop = state.getPopulation();
        if (pop < initialNumberInfected){
            System.err.println("Population too small for " + initialNumberInfected + " individuals to be initially infected." + 
                    " Decrease initial number infected or increase population size");
            return;
        }
        for(int i = 0; i < initialNumberInfected; i++){
            Agent agent = (Agent) state.network.allNodes.get(state.random.nextInt(pop));
            while(agent.age > 35 || agent.age < 25) //grab a new one till you find one between 25-35
                agent = (Agent) state.network.allNodes.get(state.random.nextInt(pop));
            
            //agent.weeksInfected = 1;
            infect(agent,null);
        }
    }
    
    public void infect(Agent infected, Agent infector){
        super.infect(infected,infector);
        
        //set a time of death based on age and Weibull
        double timeTillDeath = 52*randomWeibull(2.25,13+((15-infected.age)/10)); //given to me by Wim
        //double timeTillDeath = 52*12 + state.schedule.getTime(); //for testing purposes
        infected.attributes.put("AIDSDeath", 
                new Double(timeTillDeath+state.schedule.getTime()));
    }
    
    public double randomWeibull(double shape, double scale){
        return scale * Math.pow(-Math.log(1.0 - state.random.nextDouble()), 1.0 / shape);
    }
    
    }
