package CombinationPrevention;

import CombinationPrevention.Heuristics.*;
import CombinationPrevention.Interventions.Condom;
import CombinationPrevention.Interventions.MaleCircumcision;
import CombinationPrevention.Interventions.TestAndTreat;
import CombinationPrevention.OptimizationProblems.*;
import SimpactII.Interventions.Intervention;
import SimpactII.SimpactII;
import java.io.IOException;

/**
 *
 * @author visiting_researcher
 */
public class SolveCombinationPrevention {
    
    public static void main(String[] args){
        //make problem
        PrevalenceFit op = new PrevalenceFit();
        //MultipleCombinationPrevention op = new MultipleCombinationPrevention(args[0],Integer.parseInt(args[1]));

        //set heuristic
        //Heuristic h = new SimulatedAnnealing();
        Heuristic h = new Genetic();

        //find solution
        double[] solution = h.solve(op);
        //double[] solution = {156,416,1222,0.005,3500.0,0.01};
        
        //see how good validated model is
        SimpactII s = op.setup(solution);
        s.run();
//        try{
//            ValidatedModel.WriteHIVADPrevalence(s);
//            ValidatedModel.WriteAgeMixingRelationshipDurations(s);
//        }catch(IOException ioe){
//            System.err.println(ioe);
//            System.exit(-1);
//        }

        //print stuff for demographic / prevalence
        System.out.println("DEMOGRAPHICS:");
        ValidatedModel.PrintDemographics(s);
        System.out.println("PREVALENCE:");
        ValidatedModel.PrintPrevalence(s);
        
        //print the solution
        System.out.println("SOLUTION:");
        for(int i = 0; i< solution.length; i++)
            System.err.println(solution[i] + ", " );
        
        //display metrics for Combination Prevention
        //System.err.println("Running for output...");
        //SimpactII s = op.setup(solution);
        //s.run();
        //op.metric = "totalInfections";
        //System.out.print(op.goodness(solution, s) + ",");
        //op.metric = "totalDeaths";
        //System.out.print(op.goodness(solution, s)  + ",");
        //op.metric = "totalLifeYears";
        //System.out.println(op.goodness(solution, s));
        ////s.prevalence();
        //op.print(s,solution);
        //System.exit(1);
    }

    
    
    
}
