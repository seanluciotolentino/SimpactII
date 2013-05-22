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
        s.addAgents(SexDebutAgent.class, 1000);
        s.numberOfYears = 30;
        
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
        
        
        //test the distributions
//        double x0 = 100;
//        double lambda = 0.1;
//        Distribution dist = new Distribution() {
//        
//            //initial age population in 1980
//            private double[] dist = new double[] {0.1549, 0.2941, 0.4155, 
//                0.5198, 0.6118, 0.6905, 0.7543, 0.8088, 0.8545, 0.8932, 
//                0.9247, 0.9495, 0.9690, 0.9825, 0.9915, 0.9964, 1.0000};
//            private Distribution noise = new UniformDistribution(0, 4);
//        
//            @Override
//            public double nextValue() {
//                double r = Math.random();
//                int i = 0;
//                for(; r > dist[i]; i++){
//                    continue;
//                }
//                return (i * 5) + noise.nextValue();
//            }
//        };
//        Distribution dist = new Distribution() {
//            double scale = 10;
//            double shape = 2.25;
//            public double nextValue() {
//                return scale * Math.pow(-Math.log(1.0 - Math.random()), 1.0 / shape);
//            }
//        };
//        int num = 1000;
//        //System.out.print("hold off; bins = hist([ ");
//        System.out.print("bins = hist([ ");
//        for(int i = 0; i < num; i++)
//            //System.out.print(dist.nextValue() + ", ");
//            System.out.print(Math.round(dist.nextValue()) + ", ");
//            //System.out.print(dist.nextValue() + ", ");
//        //System.out.println("],0:5:length(x)*5); plot(bins/sum(bins)); hold all; plot(x)");
//        System.out.println("],0:25); plot(bins/sum(bins))");
        
    }
    
}
