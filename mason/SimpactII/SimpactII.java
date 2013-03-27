/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII;

import SimpactII.Agents.Agent;
import SimpactII.Graphs.*;
import SimpactII.InfectionOperators.InfectionOperator;
import SimpactII.InfectionOperators.PhaseInfectionOperator;
import SimpactII.TimeOperators.TimeOperator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import sim.engine.MakesSimState;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Distributions.*;

/**
 *
 * @author Lucio Tolentino
 */
public class SimpactII extends SimState {

    //class variables the simulation
    public Continuous2D world = new Continuous2D(1.0, 100, 100); //for geographic placement
    public Network network = new Network(false);
    public Bag myAgents = new Bag();
    
    //class variables for the population
    public int population = 2000; //default
    public double genderRatio = 0.5;
    public double numberOfYears = 30;
    public Distribution degrees = new PowerLawDistribution(2.0, 1); 
    public Distribution ages = new UniformDistribution(15.0, 65.0);//new PowerLawDistribution(1.0,3);
    public Distribution relationshipDurations = new UniformDistribution(1.0, 10.0);//new BetaDistribution(0.1,0.9);
    
    //class variables for data management
    public Bag allRelations = new Bag();
    
    //all class constructors:
    public SimpactII(long seed) {
        super(seed); //your run of Simpact II can reseed the RNG
    }
    
    public SimpactII() {
        super(System.currentTimeMillis()); //... or not reseed RNG
    }

    //all class methods go here:
    public void start() {
        super.start();
        myAgents.clear();
        world.clear();  //geographic placements
        network.clear();//sexual network
        allRelations.clear();

        //add the agents
        this.addAgents();                 

        //add the time operator
        this.addTimeOperator();
        
        //add the infection operator
        this.addInfectionOperator();
        
        //anonymous class to schedule a stop
        schedule.scheduleOnce(52*numberOfYears, new Steppable()
                {
                public void step(SimState state) { state.finish(); }
                }
        );
    }

    public void addAgents() {//add the agents
        for (int i = 0; i < population; i++) 
            new Agent(this); //add basic agents
    }
    
    public Agent addAgent() {
        return new Agent(this);
    } 
    
    public void addInfectionOperator(){
        schedule.scheduleRepeating(schedule.EPOCH, 2, new InfectionOperator(this) );
    }
    
    public void addTimeOperator(){
        schedule.scheduleRepeating(schedule.EPOCH, 1, new TimeOperator());
    }
    
    public  final void formRelationship(Agent agent1, Agent agent2){
        double duration = relationshipDurations.nextValue();
        network.addEdge(agent1, agent2, duration );
        agent1.setPartners(agent1.getPartners() + 1);
        agent2.setPartners(agent2.getPartners() + 1);
        
        //add this relationship to the relation data manager
        allRelations.add( new Relationship( agent1, agent2,
                schedule.getTime() , schedule.getTime() + duration ) );
    }
    
    public final void dissolveRelationship(Edge e) {
        Agent agent1 = (Agent) e.getFrom();
        agent1.setPartners(agent1.getPartners() - 1);
        Agent agent2 = (Agent) e.getTo();
        agent2.setPartners(agent2.getPartners() - 1);
        network.removeEdge(e);
    }   
    
    //graph functions
    public void agemixingScatter(){   new AgeMixingScatter(this);  }    
    public void demographics(){ new Demographics(this);  }
    public void formedRelations() { new FormedRelations(this);    }
    
    //CSV export methods
    public void writeCSVRelations(String filename) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("maleAge,femaleAge\n");
            int numRelations = allRelations.size();
            for(int j = 0 ; j < numRelations; j++){
                Relationship r = (Relationship) allRelations.get(j);
                if (r.getAgent1().isMale())
                    out.write(r.getAgent1().getAge() + "," + r.getAgent2().getAge() + "\n"); 
                else
                    out.write(r.getAgent2().getAge() + "," + r.getAgent1().getAge() + "\n"); 
            }
            
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }    
    }
    
    public void writeCSVPopulation(String filename) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("personID,gender,age,timeOfInfection,timeOfBirth,timeOfDeath\n");
            int numAgents = myAgents.size();
            for(int j = 0 ; j < numAgents; j++){
                Agent a = (Agent) myAgents.get(j);
                int wi;
                if(a.weeksInfected<=0) wi = 0;
                else wi = (int) ((numberOfYears*52)-a.weeksInfected) ;
                
                out.write(a.hashCode() + "," + a.isMale()+ "," + a.getAge() + "," + wi 
                         + "," + a.timeOfAddition + "," + a.timeOfRemoval + "\n");                        
            }
            
            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }    
    }

    //getters and setters / inspectors for the model
    public int getPopulation() {  return population;  }
    public void setPopulation(int val) { population = Math.max(0, val); }

    public double getGenderRatio() {  return genderRatio;  }
    public void setGenderRatio(int val) { genderRatio = Math.max(0, val); }
    
    public double[] getDNPDistribution() {
        Bag agents = network.getAllNodes();
        double[] distro = new double[agents.numObjs];
        int len = agents.size();
        for (int i = 0; i < len; i++) {
            distro[i] = ((Agent) (agents.get(i))).getDNP();
        }
        return distro;
    }
    public double[] getPartnerDistribution() {
        Bag agents = network.getAllNodes();
        double[] distro = new double[agents.numObjs];
        int len = agents.size();
        for (int i = 0; i < len; i++) {
            distro[i] = ((Agent) (agents.get(i))).getPartners();
        }
        return distro;
    }
    public double[] getAgeDistribution() {
        Bag agents = network.getAllNodes();
        double[] distro = new double[agents.numObjs];
        int len = agents.size();
        for (int i = 0; i < len; i++) {
            distro[i] = ((Agent) (agents.get(i))).getAge();
        }
        return distro;
    }

    //basic functionality methods
    public void run(String[] args) { //for running from a different file        
        //doLoop so that it uses this object
        //String[] args = {"-docheckpoint " + checkpoint}; //arg[0] = checkpoint
        final SimpactII state = this;
        doLoop(new MakesSimState()
            {
            public SimState newInstance(long seed, String[] args)
                {
                try { return  state ; } //not actually a new instance! it's this instance!
                catch (Exception e) {  throw new RuntimeException("Exception occurred while trying to construct the simulation \n" + e);}
                }
            public Class simulationClass() { return SimpactII.class; }
            }, args);        
    }
    
    public void run() {
        //no args provided        
        run( new String[0]);
    }
    public static void main(String[] args) { //for running from the command line
        doLoop(SimpactII.class, args); //just some necessary java stuff -- running this will run default        
        System.exit(0);        
    }
}