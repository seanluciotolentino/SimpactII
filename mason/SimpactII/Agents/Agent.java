package SimpactII.Agents;

import SimpactII.SimpactII;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.engine.*;
import sim.util.Bag;
import sim.util.Double2D;

/**
 *
 * @author Lucio Tolentino
 * 
 * Default Agent.  When created, an agent is given an age, a desired number of 
 * partners (DNP), and a gender from pre-specified distributions. Each step the 
 * individual is ask if they are looking ("isLooking"), and iterates through the
 * other nodes to find who this node may be looking for ("isLookingFor").  The 
 * third method, "isDesirable(Agent)" defines whether this agent wants to form a
 * relationship with the other.  
 * 
 */
public class Agent implements Steppable {

    //descriptive class variables:
    public int DNP; // = desired number of partners
    public boolean male;
    public double age;
    public HashMap attributes = new HashMap<String,Object>(); //a place for any additional attributes
    
    //charateristics which change with time
    public int partners = 0;
    public int weeksInfected = 0;
    
    //variables for keeping track in the simulation
    public Agent infector; //keep track of who infected me -- perhaps should be migrated to attributes???
    public Stoppable stoppable; //so that if individual dies, we can stop them in the schedule
    private double timeOfAddition;
    public double timeOfRemoval;
    
    public String[] args;

    //basic constructor
    public Agent(SimpactII state, HashMap<String,Object> attributes){        
        //assign random values from distribution
        this.DNP = Math.round((float) state.degrees.nextValue());
        this.male = state.random.nextDouble() <= 0.5; //default gender ratio
        this.age = state.ages.nextValue();
        this.attributes.putAll(attributes);
        
        //add self to schedule and world
        stoppable = state.schedule.scheduleRepeating(this); //schedule variable inherited from SimState -- adds the agents to the schedule (their step method will be called at each time step)
        state.network.addNode(this);                //add to network
        state.myAgents.add(this);
        timeOfAddition = state.schedule.getTime();  //added now
        timeOfRemoval  = Double.MAX_VALUE;          //removed at inf

        //for GUI purposes assign a random location in the world
        Double2D location = new Double2D(state.world.getWidth() * state.random.nextDouble() - 0.5,
                state.world.getHeight() * state.random.nextDouble());
        this.attributes.put("location", location);
        state.world.setObjectLocation(this, location );
        location.distance(location);
    }

    //methods here
    public void step(SimState s) {//what should my agent do in a step?
        SimpactII state = (SimpactII) s; //cast state to a SimpactII (from SimState)
                
        //go through other nodes, try to find partner
        Bag others = possiblePartners(state);
        int numOthers = others.size();
        for (int i = 0; i < numOthers; i++) {
            Agent other = (Agent) others.get(i);
            if ( !isLooking() ) //if this agent isn't interested, don't keep looking
                return;
            if ( isLookingFor(other) ) {
                double d1 = this.informRelationship(other);
                double d2 = other.informRelationship(this);
                state.formRelationship(this,other, Math.max(d1, d2));
            }
        } //for end
    }//step end
    
    //With whom do I form relationships
    public Bag possiblePartners(SimpactII state){
        return state.network.getAllNodes();
    }
    
    //How do I form relationships?
    public boolean isLookingFor(Agent other) {
        return other.isSeeking(this) && this.isSeeking(other); //second predicate indicates a heterosexual relationship
    }
    public boolean isLooking(){ //if not looking we don't have to check everyone
        return getPartners() < getDNP();
    }
    public boolean isSeeking(Agent other) { //indicates the question, "do you want to have a relationship with other?"
        return isLooking() && isMale() ^ other.isMale();
    }
    
    //What do I do when they are formed?
    public double informRelationship(Agent other) {
        this.partners++;
        return 0; //0 means no preference as to the length
    }
    public void informDissolution(){
        this.partners--;
    }
    //When and how am I removed from the system?
    public boolean remove(){
        return false; //use the default for the time operator
    }
    public Agent replace(SimpactII state){
        final Class c = this.getClass();
        try {
            return (Agent) c.getConstructor(new Class[]{SimpactII.class, HashMap.class}).newInstance(state, attributes);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to replace agent " + c + "\n" + e.getMessage());
        }
    }
    //How am I displayed to the world?
    public String toString(){ return this.getClass() + "" + this.hashCode(); }    
    
    //getters and setters
    public void setInfector(Agent agent){ infector = agent;  }
    public Agent getInfector(){ return infector;  }
    public int getWeeksInfected() { return weeksInfected; }
    public void setWeeksInfected(int wi) { weeksInfected = wi; }
    public int getDNP() { return DNP; }
    public void setDNP(int DNP) { this.DNP = DNP;}
    public boolean isMale() {  return male; }
    public double getAge() { return age; }
    public void setAge(double age) { this.age = age; }
    public int getPartners() { return partners; }
    public void setPartners(int partners) { this.partners = partners; }
    public double getTimeOfAddition() { return timeOfAddition;   }
}
