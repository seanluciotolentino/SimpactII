/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;
import sim.field.network.Network;
import sim.util.Double2D;

/**
 *
 * @author Lucio Tolentino
 */
public class Agent implements Steppable {

    //class variables here
    public int DNP; // = desired number of partners
    public boolean male;
    public double age;
    public int partners = 0;
    public int weeksInfected = 0;
    public Agent infector; //keep track of who infected me
    public Stoppable stoppable; //so that if individual dies, we can stop them in the schedule
    public double timeOfAddition;
    public double timeOfRemoval;

    //constructors here
    public Agent(SimpactII model) {
        this.DNP = Math.round((float) model.degrees.nextValue());
        this.male = (model.random.nextDouble() <= model.genderRatio);
        this.age = model.ages.nextValue();
        
        //add self to schedule and world
        stoppable = model.schedule.scheduleRepeating(this); //schedule variable inherited from SimState -- adds the agents to the schedule (their step method will be called at each time step)
        model.network.addNode(this); //add to network
        model.myAgents.add(this);
        timeOfAddition = model.schedule.getTime();
        timeOfRemoval  = Double.MAX_VALUE;

        //for GUI purposes:
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
            if ( isDesirable(other) ) 
                state.formRelationship(this,other);           
        } //for end
    }//step end
    
    public boolean isDesirable(Agent other) {
        return other.isLookingFor(this) && (isMale() ^ other.isMale());
    }
    
    public boolean isLooking(){ //if not looking we don't have to check everyone
        return getPartners() < getDNP();
    }

    public boolean isLookingFor(Agent other) { //other parameter allows to specify who I am looking for
        return isLooking() && isMale() ^ other.isMale();
    }
    
    public String toString(){
        return "Agent" + this.hashCode();
    }
    
    //getters and setters
    public void setInfector(Agent agent){
        infector = agent;
    }
    
    public Agent getInfector(){
        return infector;
    }
    
    public int getWeeksInfected() {
        return weeksInfected;
    }

    public void setWeeksInfected(int wi) {
        weeksInfected = wi;
    }

    public int getDNP() {
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
}
