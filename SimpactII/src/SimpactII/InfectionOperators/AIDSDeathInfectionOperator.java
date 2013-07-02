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
    public Distribution CD4AtInfection;
    public Distribution CD4AtDeath;
    
    public AIDSDeathInfectionOperator(){
    }
    
    public AIDSDeathInfectionOperator(Distribution CD4AtInfection, Distribution CD4AtDeath){
        this.CD4AtInfection = CD4AtInfection;
        this.CD4AtDeath = CD4AtDeath;
    }
    
    public void preProcess(SimpactII state){
        super.preProcess(state);
        this.state = state;     
        if(this.CD4AtInfection == null)
            CD4AtInfection = new UniformDistribution(1400, 1600, state.random);
        if(this.CD4AtDeath == null)
            CD4AtDeath = new UniformDistribution(0, 50, state.random);
    }
    
    public void performInitialInfections(SimpactII state){
        //perform initial infections in 25-35
        //int pop = state.getPopulation();
        int pop = state.network.allNodes.size();
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
        infected.attributes.put("AIDSDeath", new Double(timeTillDeath+state.schedule.getTime()));
        infected.attributes.put("CD4AtInfection", CD4AtInfection.nextValue() );
        infected.attributes.put("CD4AtDeath", CD4AtDeath.nextValue() );
    }
    
    public double randomWeibull(double shape, double scale){
        return scale * Math.pow(-Math.log(1.0 - state.random.nextDouble()), 1.0 / shape);
    }
    
}
