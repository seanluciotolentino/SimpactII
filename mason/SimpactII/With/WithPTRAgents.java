package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.PTRAgent;
import SimpactII.DataStructures.Relationship;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 */
public class WithPTRAgents extends SimpactII{
    
    public static void main(String[] args){
        final SimpactII model = new WithPTRAgents();
        model.timeOperator = new TimeOperator()
            {
            public Agent replace(SimpactII state, Agent agent){ return new PTRAgent(model,2); }
            };
        model.population = 100;
        model.numberOfYears = 10;
        model.run();
        model.formedRelations(); 
    }
    
    //change how / what agents are added
    public void addAgents(){
        for(int i = 0; i < population ; i++)
            new PTRAgent(this,2);
    }

}
