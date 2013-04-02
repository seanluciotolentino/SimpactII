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
public class WithPTRAgents{

    public static void main(String[] args){
        SimpactII model = new SimpactII();
        model.addAgents(PTRAgent.class, 100, new String[] {"2"} );
        model.numberOfYears = 10;
        model.run();
        model.formedRelations(); 
    }

}
