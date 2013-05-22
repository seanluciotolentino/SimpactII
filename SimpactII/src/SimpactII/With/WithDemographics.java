package SimpactII.With;

import SimpactII.Agents.*;
import SimpactII.InfectionOperators.*;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.*;
import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Distributions.*;

/**
 *
 * @author Lucio Tolentino
 */
public class WithDemographics {
    
    public static void main (String[] args){
        SimpactII s = new SimpactII();
        s.numberOfYears = 30;
        
        //add special sex debut agents
        Distribution sexualDebutAges = new UniformDistribution(13, 18);
        HashMap<String,Object> attri = new HashMap<>();
        for(int i =0 ; i < 1000; i++){
            attri.put("debutAge", sexualDebutAges.nextValue());
            s.addAgents(SexDebutAgent.class, 1,attri);
        }
        
        
        //set the initial age distribution based on data
        s.ages = new Distribution() {
            //initial age population in 1980
            private double[] dist = new double[] {0.1549, 0.2941, 0.4155, 
                0.5198, 0.6118, 0.6905, 0.7543, 0.8088, 0.8545, 0.8932, 
                0.9247, 0.9495, 0.9690, 0.9825, 0.9915, 0.9964, 1.0000};
            private Distribution noise = new UniformDistribution(0, 4);
        
            @Override
            public double nextValue() {
                double r = Math.random();
                int i = 0;
                for(; r > dist[i]; i++){ continue; }
                return (i * 5) + noise.nextValue();
            }
        };
        
        //modified operators
        s.timeOperator = new DemographicTimeOperator();
        s.infectionOperator = new AIDSDeathInfectionOperator();
        
        //check for correctness
        //s.launchGUI();
        s.run();
        s.demographics();
        s.prevalence();
        s.agemixingScatter();
        
    }
    
}
