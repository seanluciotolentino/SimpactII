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
public class WithAgeMixing {
       
    //main method
    public static void main(String[] args){
        SimpactII model = new SimpactII();
        model.addAgents(AgeAgent.class, 100, new String[] {"5","5"} );
        model.addAgents(AgeAgent.class, 100, new String[] {"2","-5"} );
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
