package SimpactII.With;

import SimpactII.Agents.BandAgeAgent;
import SimpactII.Agents.Agent;
import SimpactII.Agents.PTRAgent;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
import java.util.HashMap;
import SimpactII.Distributions.*;

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
        HashMap ageAttributes = new HashMap<String,Object>();
        ageAttributes.put("band", 2.0);
        ageAttributes.put("offset", -5.0);
        model.addAgents(BandAgeAgent.class, 100, ageAttributes );
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
