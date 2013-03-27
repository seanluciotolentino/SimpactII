/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.AgeAgent;
import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
import sim.util.Distributions.UniformDistribution;

/**
 *
 * @author visiting_researcher
 */
public class WithAgeMixing extends SimpactII{
    
    private double band = 5;
    private double offset = 5;
    
    public WithAgeMixing(){
        super();
        this.population = 1000;
        this.numberOfYears = 10;        
        this.ages = new UniformDistribution(15,65);
        this.relationshipDurations = new UniformDistribution(1,5);
    }
    
    public void addAgents(){
        for (int i = 0; i < population; i++) {
            new AgeAgent(this, band, offset); //agent class below
        }
    }
    
    public Agent addAgent(){ //called by replace method which will add a 15 year old
        return new AgeAgent(this,band,offset,15);
    }
    
    public static void main(String[] args){
        WithAgeMixing amp = new WithAgeMixing();
        amp.run();
        amp.agemixingScatter();
        String filename = "relations" + amp.band + "" + amp.offset + ".csv";
        amp.writeCSVRelations(filename);
        filename = "population" + amp.band + "" + amp.offset + ".csv";
        amp.writeCSVPopulation(filename);
    }
    
    
    
    
}
