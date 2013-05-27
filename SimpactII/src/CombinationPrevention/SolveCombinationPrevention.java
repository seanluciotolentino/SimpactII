package CombinationPrevention;

import CombinationPrevention.Heuristics.*;
import CombinationPrevention.Interventions.Condom;
import CombinationPrevention.Interventions.MaleCircumcision;
import CombinationPrevention.Interventions.TestAndTreat;
import CombinationPrevention.OptimizationProblems.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class SolveCombinationPrevention {

    public static void main(String[] args) throws InterruptedException {
        //set parameters
        String metric = "totalInfections";//args[0];//
        int population = 1000;//Integer.parseInt(args[1]);//
        //System.out.println("metric: " + metric + " population: " + population);
        
        //set optimization problem
        //OptimizationProblem op = new CondomCombinationPrevention(metric);
        OptimizationProblem op = new MultipleCombinationPrevention(metric,population);

        //set heuristic
        Heuristic h = new SimulatedAnnealing();
        //Heuristic h = new Genetic();

        //find solution
        //double[] solution = h.solve(op);
        //double[] solution = op.getX0();
        //double[] solution = loadSolution();
        double[] solution = new double[24];
        
        //display metrics
        System.err.println("Running for output...");
        SimpactII s = op.setup(solution);
        s.run();
        op.setMetric("totalInfections");
        System.out.print(op.goodness(solution, s) + ",");
        op.setMetric("totalDeaths");
        System.out.print(op.goodness(solution, s)  + ",");
        op.setMetric("totalLifeYears");
        System.out.println(op.goodness(solution, s));
        //s.prevalence();
        //print(s,op,solution);
        System.exit(1);
    }
    
    private static double[] loadSolution(){
        return new double[] {372.2062786029771, 812.6642923500908, 0.09659884012126432, 517.3682196100041, 402.334351619127, 0.3759787628565957, 55.48525162749907, 831.6261506228556, 0.2984314321882403, 78.72582564454689, 601.4392635406008, 457.67069938298386, 468.06925889579276, 484.29391380518473, 12.560925835941068, 28.17109926920436, 6.845347017615481, 990.6070934599443, 6.7461570828401145, 334.37388064002437, 46.16227924074221, 780.5492405827679, 40.018410734918966, };
        
        
        //return new double[] {1000,1000,0.5, 0,0,0.5,  0,0,0.5,   0,  0,  0,  0,  0,0,  0,0,  0,0,  0,0,  0,0};
        //return new double[] {1000,1000,0.5, 0,0,0.5,  0,0,0.5,   0,  0,  0,  0,  0,0,  0,0,  0,0,  0,0,  0,0};
    }
    
    private static void print(SimpactII s, OptimizationProblem op, double[] combo){
        //top bar stuff
        System.err.println(op.getMetric() + ":," + op.goodness(combo, s) + ",");
        double totalCost = op.cost(combo, s);
        System.err.println("Cost:, " + totalCost + " ,");
        
        //actual interventions        
        System.err.println("Parameter Value, Total Cost, Percentage of Budget");
        //Test and treat
        Intervention intervention; 
        double total = 0;
        String[] targets = new String[] {"generalPopulation","msm","sexWorker"};
        for(int i = 0; i< targets.length; i++){
            System.err.println(combo[(3*i)] + ", ," );
            System.err.println(combo[(3*i)+1]   + ", ," );
            System.err.println(combo[(3*i)+2]   + ", ," );
            
            //intervention total
            intervention = (Intervention) s.myInterventions.get(i);
            System.err.print("," + intervention.getSpend() );
            total+=intervention.getSpend();
            System.err.println(", " + intervention.getSpend() / totalCost);
        }
        System.err.println("," + total + "," + total / totalCost);
        
        //MC
        total = 0;
        targets = new String[] {"generalPopulation","msm","young","highRisk"};
        for(int i = 0; i< targets.length; i++){
            System.err.println(combo[i+9]     + ", " );
                        
            //intervention total
            intervention = (Intervention) s.myInterventions.get(i+3);
            System.err.print("," + intervention.getSpend() );
            total+=intervention.getSpend();
            System.err.println(", " + intervention.getSpend() / totalCost);
        }
        System.err.println("," + total + "," + total / totalCost);
        
        //condom
        total = 0;
        targets = new String[] {"generalPopulation","msm","sexWorker","young","highRisk"};
        for(int i = 0; i< targets.length; i++){
            System.err.println(combo[(2*i)+13]     + ", " );
            System.err.println(combo[(2*i)+14]   + ", " );
            
            //intervention total
            intervention = (Intervention) s.myInterventions.get(i+7);
            double thisCost = intervention.getSpend() ;
            System.err.print("," + thisCost);
            total+=thisCost;
            System.err.println(", " + thisCost / totalCost);
        }
        System.err.println("," + total + "," + total / totalCost);
    }
}
