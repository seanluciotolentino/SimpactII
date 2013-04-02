package SimpactII.With;

import SimpactII.Agents.AgeAgent;
import SimpactII.Agents.Agent;
import SimpactII.Agents.PTRAgent;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
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
            new AgeAgent(this,band,offset);
    }
    
        //main method
    public static void main(String[] args){
        final WithAgeMixing model = new WithAgeMixing();
        model.timeOperator = new TimeOperator()
            {
            public Agent replace(SimpactII state, Agent agent){ return new AgeAgent(model,model.band,model.offset); }
            };
        model.population = 1000;
        model.numberOfYears = 10;        
        model.ages = new UniformDistribution(15,65);
        model.relationshipDurations = new UniformDistribution(1,5);
        model.run();
        
        //after the run produce results
        model.agemixingScatter();
        //String filename = "relations" + amp.band + "" + amp.offset + ".csv";
        //amp.writeCSVRelations(filename);
        //filename = "population" + amp.band + "" + amp.offset + ".csv";
        //amp.writeCSVPopulation(filename);
    }
}
