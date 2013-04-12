package SimpactII.TimeOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import SimpactII.With.WithPTRAgents;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * Default time operator.  This operator goes through every Agent and increments
 * their age by 1 week (1/52 years). Then it goes through the Agent's relationships
 * and decrements their duration by 1/2 week (the other half is decremented when
 * the other partner has their age incremented). Any relationships which have 
 * negative durations (are over) are removed from the system. This is done by a 
 * call to the main model "dissolveRelationship".  
 * 
 */
public class TimeOperator implements Steppable{
    
    private double MAX_AGE = 65;
    
    public TimeOperator(int MAX_AGE){
        this.MAX_AGE = MAX_AGE;
    }
    
    public TimeOperator(){
    }
        
    public void step(SimState s){
        SimpactII state = (SimpactII) s;
        
        //increments agent ages
        Bag agents = state.network.getAllNodes();
        int numOthers = agents.size();
        for(int i = 0 ; i < numOthers; i++){
            //increment ages
            Agent agent = (Agent) agents.get(i);
            agent.setAge(agent.getAge() + 1.0/52);
            
            //decrement relations
            Bag relations = state.network.getEdges(agent, new Bag() );
            for(int j = 0 ; j < relations.size(); j++){
                Edge e = (Edge) relations.get(j);
                e.setInfo((double) e.getWeight()- 0.5 ); //decrement by 0.5 b/c this is called twice (once by each node of the edge)
                if (e.getWeight() <= 0)
                    state.dissolveRelationship(e);               
            }
            
            //check some removal condition
            if(remove(agent)){ //if some removal condition is met
                //somehow replace individual:
                Agent newAgent = replace(state,agent);
                //newAgent.attributes.putAll(agent.attributes); ////copy over attributes -- some attributes might not want to copy directly, i.e. extra condoms??? location???
                state.network.addNode(newAgent); 

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
        }//end for loop
    }
    
    public void preProcess(SimpactII state) {
        return;
    }
    
    //The two overridable methods
    public boolean remove(Agent agent){
        return agent.remove() || agent.getAge() > MAX_AGE;        
    }
    
    public Agent replace(SimpactII state, Agent agent){
        //return state.addAgent(agent.getClass());
        return agent.replace(state);
        //        final Class c = agent.getClass();
        //        try {
        //            return (Agent) c.getConstructor(new Class[] {SimpactII.class}).newInstance(state);
        //            //return (Agent) (c.getConstructor(new Class[] { Long.TYPE }).newInstance(new Object[] { new Long(seed) } ));
        //        } catch (Exception e) {
        //            throw new RuntimeException("Exception occurred while trying to replace agent " + c + "\n" + e);
        //        }
    }  
}
