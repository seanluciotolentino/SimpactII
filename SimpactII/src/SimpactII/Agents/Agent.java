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
 * Default Agent. When created, an agent is given an age, a desired number of
 * partners (DNP), and a gender from pre-specified distributions. Each step the
 * individual is ask if they are looking ("isLooking"), and iterates through the
 * other nodes to find who this node may be looking for ("isLookingFor"). The
 * third method, "isDesirable(Agent)" defines whether this agent wants to form a
 * relationship with the other.
 *
 */
public class Agent implements Steppable {

    //descriptive class variables:
    public double DNP; // = desired number of partners
    public boolean male;
    public double age;
    public HashMap attributes = new HashMap<String, Object>(); //a place for any additional attributes
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
    public Agent(SimpactII state, HashMap<String, Object> attributes) {
        //grab attributes
        this.attributes.put("genderRatio", 0.5);
        this.attributes.putAll(attributes); //this might replace gender ratio
        
        //assign random values from distribution
        this.DNP = (double) state.degrees.nextValue();
        this.male = state.random.nextDouble() <= (double) this.attributes.get("genderRatio"); //default gender ratio
        this.age = state.ages.nextValue();
        

        //add self to schedule and world
        stoppable = state.schedule.scheduleRepeating(this); //schedule variable inherited from SimState -- adds the agents to the schedule (their step method will be called at each time step)
        state.network.addNode(this);                //add to network
        state.myAgents.add(this);
        timeOfAddition = state.schedule.getTime();  //added now
        timeOfRemoval = Double.MAX_VALUE;          //removed at inf

        //for GUI purposes assign a random location in the world
        Double2D location = new Double2D(state.world.getWidth() * state.random.nextDouble() - 0.5,
                state.world.getHeight() * state.random.nextDouble());
        this.attributes.put("location", location);
        state.world.setObjectLocation(this, location);
        location.distance(location);
    }

    //methods here
    /**
     * Lets the agent perform the actions as he / she would in a week. 
     * @param state
     */
    public void step(SimState s) {
        SimpactII state = (SimpactII) s; //cast state to a SimpactII (from SimState)

        //go through other nodes, try to find partner
        Bag others = possiblePartners(state);
        int numOthers = others.size();
        for (int i = 0; i < numOthers; i++) {
            Agent other = (Agent) others.get(i);
            if (!isLooking()) //if this agent isn't interested, don't keep looking
            {
                return;
            }
            if (isLookingFor(other)) {
                double d1 = this.informRelationship(other);
                double d2 = other.informRelationship(this);
                state.formRelationship(this, other, Math.max(d1, d2));
            }
        } //for end
    }//step end

    /**
     * Specifies which agents this agent is interested in forming a relationship
     * with. The default agent is interested in all other agents. The default 
     * agent is interested in all other agents.
     *
     * @param state
     * @return Bag of possible partners
     */
    public Bag possiblePartners(SimpactII state) {
        return state.network.getAllNodes();
    }
    /**
     * Base check to see if this agent is interested in forming relationships
     * this round.
     *
     * @return boolean
     */
    public boolean isLooking() { //if not looking we don't have to check everyone
        return getPartners() < getDNP();
    }
    /**
     * When going through the bag of possible partners, ask this agent if he /
     * she is looking for the agent <b>other</b>.
     *
     * @param other
     * @return boolean value of whether this agent is looking for <b>other</b>
     */
    public boolean isLookingFor(Agent other) {
        return other.isSeeking(this) && this.isSeeking(other); //second predicate indicates a heterosexual relationship
    }

    /**
     * Called when another agent wants to form a relationship with this agent.
     * Indicates the question, "does this agent want to have a relationship with
     * <b>other</b> agent?"
     *
     * @param other
     * @return boolean
     */
    public boolean isSeeking(Agent other) {
        return isLooking() && isMale() ^ other.isMale();
    }

    /**
     * When a relationship is formed this method is called in order to increment
     * this agents number of partners. If the agent has a preference for the
     * length of the relationship, a non-zero double will be returned, else zero
     * will be returned.
     *
     * @param other
     * @return double indicating preference of relationship length
     */
    public double informRelationship(Agent other) {
        this.partners++;
        return 0; //0 means no preference as to the length
    }
    /*
     * Decrement number of partners.
     */

    public void informDissolution() {
        this.partners--;
    }
    /*
     * Return whether the removal criteria of the agent has been met.
     */

    public boolean remove() {
        return false; //use the default for the time operator
    }
    /*
     * Returns an agent which replaces this agent after he/she has been removed.
     */

    public Agent replace(SimpactII state) {
        final Class c = this.getClass();
        try {
            Agent a = (Agent) c.getConstructor(new Class[]{SimpactII.class, HashMap.class}).newInstance(state, attributes);
            a.age = 15;
            return a;
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to replace agent " + c + "\n" + e.getMessage());
        }
    }
    //How am I displayed to the world?

    public String toString() {
        return this.getClass() + "" + this.hashCode();
    }

    //getters and setters
    public void setInfector(Agent agent) {
        infector = agent;
    }

    public Agent getInfector() {
        return infector;
    }

    public int getWeeksInfected() {
        return weeksInfected;
    }

    public void setWeeksInfected(int wi) {
        weeksInfected = wi;
    }

    public double getDNP() {
        return DNP;
    }

    public void setDNP(int DNP) {
        this.DNP = DNP;
    }

    public boolean isMale() {
        return male;
    }

    public double getAge() {
        return age;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public int getPartners() {
        return partners;
    }

    public void setPartners(int partners) {
        this.partners = partners;
    }

    public double getTimeOfAddition() {
        return timeOfAddition;
    }
}
