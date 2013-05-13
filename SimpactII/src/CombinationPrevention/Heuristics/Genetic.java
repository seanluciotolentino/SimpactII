/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CombinationPrevention.Heuristics;

import CombinationPrevention.OptimizationProblems.OptimizationProblem;
import ec.util.MersenneTwisterFast;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class Genetic implements Heuristic{
    
    private MersenneTwisterFast rand;
    
    //heuristic parameters    
    private int populationLimit = 10; //number of initial random solutions
    private int numCrossOverGen = 5; //the best to pick 
    private int numHoldOverGen = 3; //new blood = populationLimit - numCrossOverGen - numHoldOverGen
    private int generateLimit = 10;
    
    //optimization problem
    private int solutionLength;
    private double[] min;
    private double[] max;
    
    
    public Genetic(){
        rand = new MersenneTwisterFast();
    }
    
    public double[] solve(OptimizationProblem op){
        //initialization
        solutionLength = op.getX0().length;
        min = op.getLB();
        max = op.getUB();
        
        //initialize priority queue with ability to compare solutions
        PriorityQueue pq = new PriorityQueue(populationLimit, new Comparator<Object[]>(){
            public int compare(Object[] o1, Object[] o2) {
                if( (double) o1[1] < (double) o2[1])
                    return -1;
                else if( (double) o1[1] < (double) o2[1])
                    return 1;
                else
                    return 0;
            }            
        });
    
        //generate n random solutions
        Bag solutions = new Bag();
        for(int i = 0; i < populationLimit; i++){
            double[] newSolution = randomSolution();
            solutions.add(newSolution);
        }
        
        //mainloop
        double[] bestSolution = randomSolution(); //double[] of zeros
        double bestFitness = Double.POSITIVE_INFINITY;
        for(int i =0; i < generateLimit; i++){
            System.out.println( " ============== generation " + i + "===========");
            //evaluate each and add to the priority queue
            for( int j = 0; j < populationLimit; j++){
                double[] thisSolution = (double[]) solutions.get(j);
                double fitness = op.run( thisSolution );
                pq.add( new Object[] {thisSolution, fitness}  ); //I'm not sure how this will work...
            }
            
            //check if better than the best
            Object[] topSolution = (Object[]) pq.peek();
            if( (double) topSolution[1] < bestFitness){
                System.out.println( " new best --> " + topSolution[1] );
                bestSolution = (double[]) topSolution[0];
                bestFitness = (double) topSolution[1];
            }
            
            //prepare the next generation
            solutions.clear();
            Bag best = new Bag();
            for( int k = 0; k < numHoldOverGen; k++){
                Object[] holdOver = (Object[]) pq.remove();
                solutions.add( holdOver[0] );
                best.add( holdOver[0] );
            }
            
            //grab the "m" best...
            while(solutions.size() < numHoldOverGen + numCrossOverGen)
                solutions.add( crossOver(best) );
            
            //add new blood
            while( solutions.size() < populationLimit)
                solutions.add( randomSolution() );
        }
            
        return bestSolution;
    }

    private double[] randomSolution() {
        double[] newSolution = new double[solutionLength];
        for(int j = 0; j<newSolution.length; j++){
            double min = this.min[j];
            double max = this.max[j];
            newSolution[j] = min + (rand.nextDouble()* ( max - min) ) ;//UPDATE THIS
        }
        return newSolution;
    }

    private double[] crossOver(Bag best) {
        double[] sol1 = (double[]) best.get( rand.nextInt( best.size() ) );
        double[] sol2 = (double[]) best.get( rand.nextInt( best.size() ) );
        int crossOverSpot = rand.nextInt(solutionLength);
        
        double[] newSolution = new double[solutionLength];
        for( int i = 0; i < solutionLength; i++)
            newSolution[i] = (i < crossOverSpot)? sol1[i]: sol2[i];
        
        return newSolution;    
    }

        

    
}
