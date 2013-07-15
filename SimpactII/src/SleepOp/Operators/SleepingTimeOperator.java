package SleepOp.Operators;

import SleepOp.Agents.LocalSleepingAgent;
import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import SimpactII.TimeOperators.TimeOperator;
import SleepOp.Agents.SleepingAgentInterface;
import sim.engine.SimState;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio
 */
public class SleepingTimeOperator extends TimeOperator{
    
    protected SimpactII state;
    
    public void preProcess(SimpactII state){
        this.state = state;
        super.preProcess(state);
    }
    
    public void replace(SimpactII state, Agent agent){
        Agent a = agent.replace(state);
        a.age = 15;
    }
    
    
    public void step(SimState s){
        SimpactII state = (SimpactII) s;
        
        //increments agent ages
        Bag agents = state.myAgents; //must increment the ages of everyone
        int numOthers = agents.size();
        for(int i = 0 ; i < numOthers; i++){
            //increment ages
            Agent agent = (Agent) agents.get(i);
            agent.setAge(agent.getAge() + 1.0/52);
            
            //check some removal condition
            Bag relations;
            if((agent.timeOfRemoval >= Double.MAX_VALUE) && remove(agent) ){ //if some removal condition is met && not already removed
                //somehow replace individual:
                replace(state,agent);
                
                //remove them from the world:
                agent.stoppable.stop(); //stop them from being scheduled
                agent.timeOfRemoval = state.schedule.getTime();
                relations = state.network.getEdges(agent, new Bag() );
                for(int k = 0 ; k < relations.size(); k++){
                    Edge e = (Edge) relations.get(k);
                    state.dissolveRelationship(e); //dissolve all of their relations
                }
                state.network.removeNode(agent);
                state.world.remove(agent);
            } //end if statement
            
            //decrement relations -- skip sleepers
            SleepingAgentInterface sagent = (SleepingAgentInterface) agent;
            if(sagent.getSleeping() )
                continue;
            
            //if you've made it here, you're not sleeping, use appropriate decrement
            relations = state.network.getEdges(agent, new Bag() );
            for(int j = 0 ; j < relations.size(); j++){
                Edge e = (Edge) relations.get(j);
                SleepingAgentInterface other = (SleepingAgentInterface) e.getOtherNode(agent);
                double decrement = (other.getSleeping() )? 1:0.5; //fully decrement relations with sleeping nodes -- they can't decrement their part
                e.setInfo((double) e.getWeight()- decrement ); //decrement by 0.5 b/c this is called twice (once by each node of the edge)
                if (e.getWeight() <= 0)
                    state.dissolveRelationship(e);                
            }
            
        }//end for loop
    }//end step
    
        
    
    
}
