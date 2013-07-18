/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SleepOp.Operators;

import SimpactII.Agents.Agent;
import SimpactII.DataStructures.Relationship;
import SimpactII.SimpactII;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio
 */
public class ParallelRelationshipOperator implements Steppable{
    
    public void preProcess(SimpactII s) {
        s.executor = Executors.newFixedThreadPool(s.processors);
    }
    
    public void step(SimState state) {
        SimpactII s = (SimpactII) state;
        
        /*
         * Wait for the agents to finish forming relationships: //if you don't 
         * wait till they're done the other operators are going to have problems 
         * eventually.         
         */
        s.executor.shutdown(); //shutting down means no more processes added
        try {
            if( !s.executor.awaitTermination(10, TimeUnit.SECONDS) ) {  // doesn't work if executor isn't shut down
                System.err.println("Simulation terminated: One of the agents took too long to complete");
                System.exit(-1);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelRelationshipOperator.class.getName()).log(Level.SEVERE, null, ex);
        }
        s.executor = Executors.newFixedThreadPool(s.processors); //replace executor with a new (one that hasn't been closed)

        /*
         * Fix relationships that were made because of parallelization -- go 
         * through most recent relationships and revert 
         * 
         */
        double now = s.schedule.getTime();
        if(s.myRelations.size() <= 0){ return; } //don't do this if there's no relationships
        for(int i = s.myRelations.numObjs-1; i>=0; i-=1){ //go through myRelations backwards to get the most recent
            Relationship r = (Relationship) s.myRelations.get(i);
            if(r.getStart() < now)
                return; //there's no more relationships that started this timestep
            
            //check the agents of this relationship are okay
            Agent a1 = r.getAgent1();
            Agent a2 = r.getAgent2();
            if(a1.partners > a1.DNP || a2.partners > a2.DNP){ //relationship broke someone
                //delete the edge (does it have to be done in this ad hoc way?)
                Bag possibleEdges = s.network.getEdgesIn(a1);
                for( int j = 0; j<possibleEdges.size(); j++ ){
                    Edge e = (Edge) possibleEdges.get(j);
                    if(e.getFrom().equals(a2) )
                        s.dissolveRelationship(e);
                } 
            }//end if relationship illegal check
        }//end new relationships loop
    }//end step
}
