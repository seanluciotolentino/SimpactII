package SimpactII.Agents;

import SimpactII.SimpactII;
import java.lang.reflect.InvocationTargetException;
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
    
    //charateristics which change with time
    public int partners = 0;
    public int weeksInfected = 0;
    
    //variables for keeping track in the simulation
    public Agent infector; //keep track of who infected me
    public Stoppable stoppable; //so that if individual dies, we can stop them in the schedule
    public double timeOfAddition;
    public double timeOfRemoval;

    //basic constructor
    public Agent(SimpactII model) {
        //assign random values from distribution
        this.DNP = Math.round((float) model.degrees.nextValue());
        this.male = model.random.nextDouble() <= model.genderRatio;
        this.age = model.ages.nextValue();
        
        //add self to schedule and world
        stoppable = model.schedule.scheduleRepeating(this); //schedule variable inherited from SimState -- adds the agents to the schedule (their step method will be called at each time step)
        model.network.addNode(this);                //add to network
        model.myAgents.add(this);
        timeOfAddition = model.schedule.getTime();  //added now
        timeOfRemoval  = Double.MAX_VALUE;          //removed at inf

        //for GUI purposes assign a random location in the world
        model.world.setObjectLocation(this,
            new Double2D(model.world.getWidth() * model.random.nextDouble() - 0.5,
                model.world.getHeight() * model.random.nextDouble()));
    }

    //methods here
    public void step(SimState s) {//what should my agent do in a step?
        SimpactII state = (SimpactII) s; //cast state to a SimpactII (from SimState)
                
        //go through other nodes, try to find partner
        Bag others = state.network.getAllNodes();
        int numOthers = others.size();
        for (int i = 0; i < numOthers; i++) {
            Agent other = (Agent) others.get(i);
            if ( !isLooking() ) //if this agent isn't interested, don't keep looking
                return;
            if ( isDesirable(other) ) {
                double d1 = this.informRelationship(other);
                double d2 = other.informRelationship(this);
                state.formRelationship(this,other, Math.max(d1, d2));
            }
        } //for end
    }//step end
    
    //How do I form relationships?
    public boolean isDesirable(Agent other) {
        return other.isLookingFor(this) && (isMale() ^ other.isMale()); //second predicate indicates a heterosexual relationship
    }
    public boolean isLooking(){ //if not looking we don't have to check everyone
        return getPartners() < getDNP();
    }
    public boolean isLookingFor(Agent other) { //other parameter allows to specify who I am looking for
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
            return (Agent) c.getConstructor(new Class[] {SimpactII.class}).newInstance(state);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to replace agent " + c + "\n" + e);
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
}
