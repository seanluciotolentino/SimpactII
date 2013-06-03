package SimpactII.With;

import CombinationPrevention.Interventions.TestAndTreat;
import SimpactII.Agents.*;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.*;
import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;
import SimpactII.Distributions.*;

/**
 *
 * @author Lucio Tolentino
 */
public class WithDemographics {
    
    public static void main (String[] args){
        final SimpactII s = new SimpactII();
        s.numberOfYears = 30; // (1) 1985 | (30) 2015 | (45) 2030
        
        //add special sex debut agents
        Distribution sexualDebutAges = new UniformDistribution(13, 18,s.random);
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
            private Distribution noise = new UniformDistribution(0, 4,s.random);
        
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
        s.infectionOperator = new InterventionInfectionOperator(new AIDSDeathInfectionOperator());
        
        //test interventions
        //Intervention testAndTreat = new TestAndTreat("generalPopulation", 1000,1000,0.9);
        //s.addIntervention(testAndTreat);
        
        //check for correctness
        //s.launchGUI();
        s.run();
        s.demographics();
        s.prevalence();
        //s.agemixingScatter();
        
//        Distribution dist = new ExponentialDecay(52, 0.5);
//        int num = 1000;
//        //System.out.print("hold off; bins = hist([ ");
//        System.out.print("bins = hist([ ");
//        for(int i = 0; i < num; i++)
//            //System.out.print(dist.nextValue() + ", ");
//            System.out.print(Math.round(dist.nextValue()) + ", ");
//            //System.out.print(dist.nextValue() + ", ");
//        //System.out.println("],0:5:length(x)*5); plot(bins/sum(bins)); hold all; plot(x)");
//        System.out.println("],0:52:(52*10)); plot(bins/sum(bins))");
        
    }
    
}
