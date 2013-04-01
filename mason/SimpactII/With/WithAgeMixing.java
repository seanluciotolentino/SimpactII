package SimpactII.With;

import SimpactII.Agents.AgeAgent;
import SimpactII.Agents.Agent;
import SimpactII.Agents.PTRAgent;
import SimpactII.SimpactII;
import sim.util.Distributions.UniformDistribution;

/**
 *
 * @author Lucio Tolentino
 * 
 * An extension of SimpactII that Wim requested for a proposal. It creates "AgeAgents"
 * with a certain band and offset by overriding the addAgents() method. After the
 * run, we produce the age-mixing scatter and output infection / relationship 
 * information to a csv.
 * 
 */
public class WithAgeMixing extends SimpactII{
    
    public double band = 5;
    public double offset = 5;
      
    //overriding methods
    public void addAgents(){
        //NOTE that addNAgents cannot be used here because an age agent requires
        //additional parameters and it would confuse the method
        for(int i = 0; i < population ; i++)
            new AgeAgent(this,5,5);
    }
    //main method
    public static void main(String[] args){
        WithAgeMixing amp = new WithAgeMixing();
        amp.population = 1000;
        amp.numberOfYears = 10;        
        amp.ages = new UniformDistribution(15,65);
        amp.relationshipDurations = new UniformDistribution(1,5);
        amp.run();
        
        //after the run produce results
        amp.agemixingScatter();
        //String filename = "relations" + amp.band + "" + amp.offset + ".csv";
        //amp.writeCSVRelations(filename);
        //filename = "population" + amp.band + "" + amp.offset + ".csv";
        //amp.writeCSVPopulation(filename);
    }
    
    
    
    
}
