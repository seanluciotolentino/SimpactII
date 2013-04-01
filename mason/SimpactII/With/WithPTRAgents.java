package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.PTRAgent;
import SimpactII.DataStructures.Relationship;
import SimpactII.SimpactII;
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
        SimpactII model = new WithPTRAgents();
        model.population = 100;
        model.numberOfYears = 10;
        model.run();
        model.formedRelations();
    }
    
    //change how / what agents are added
    public void addAgents(){
        for(int i = 0; i < population ; i++)
            new PTRAgent(this,2);
        
        //add an agent to the schedule that resets partnersThisYear to 0 at the beginning of each year
        //alternatively this could be in the addMoreOperators() method
        schedule.scheduleRepeating(new Steppable() {
            public void step(SimState s) {
                SimpactII state = (SimpactII) s;
                Bag agents = state.network.getAllNodes();
                int numAgents = agents.size();
                for(int i = 0; i < numAgents; i++){
                    if(agents.get(i).getClass() == PTRAgent.class){ //only do this for PTRAgents!
                        PTRAgent ptrAgent = (PTRAgent) agents.get(i);
                        ptrAgent.resetPartners();
                        //System.out.println( ptrAgent + " was reset" );
                    }
                }
            }//end step method
            },52.0); //he only wakes up every 52 steps
    }
    
    //change relationship formation / dissolution so that partners this year is incremented
    public void formRelationship(Agent agent1, Agent agent2){
        //form relation in normal way:
        super.formRelationship(agent1,agent2);
        
        //but also update agents partnersThisYear variable
        if (agent1.getClass() == PTRAgent.class){
            PTRAgent a1 = (PTRAgent) agent1;
            a1.partnersThisYear++;
        }
        if (agent2.getClass() == PTRAgent.class){
            PTRAgent a2 = (PTRAgent) agent2;
            a2.partnersThisYear++;
        }
    }
}
