package SimpactII;

import SimpactII.Agents.Agent;
import SimpactII.DataStructures.Relationship;
import SimpactII.Graphs.*;
import SimpactII.InfectionOperators.InfectionOperator;
import SimpactII.Interventions.Intervention;
import SimpactII.TimeOperators.TimeOperator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import javax.swing.JFrame;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.field.network.*;
import sim.util.Bag;
import sim.util.Distributions.*;

/**
 *
 * @author Lucio Tolentino
 *
 * This is the main SimpactII model. It has all the default values. Additional
 * models that incorporate SimpactII should extends (inherit from) this class.
 * The default version adds basic agents to the schedule and lets them form
 * relationships based soley on gender and desired number of partners (pulled
 * from assigned distributions). A time operators controls the ageing of
 * individuals and an infection operator controls infections.
 */
public class SimpactII extends SimState {

    //class variables visualization
    public Continuous2D world = new Continuous2D(1.0, 100, 100); //for geographic placement
    public Network network = new Network(false); //the sexual network
    
    //class variables for the population
    private int population = 0; //default
    private Bag subPopulationTypes = new Bag(); //perhaps these three can be reduced to one for simplicity...???
    private Bag subPopulationNum = new Bag();
    private Bag subPopulationArgs = new Bag();
    public double numberOfYears = 30;
    public Distribution degrees = new PowerLawDistribution(2.0, 1);
    public Distribution ages = new UniformDistribution(15.0, 65.0);//new PowerLawDistribution(1.0,3);
    public Distribution relationshipDurations = new UniformDistribution(1.0, 10.0);//new BetaDistribution(0.1,0.9);
    
    //class variables for main operations
    public TimeOperator timeOperator = new TimeOperator();
    public InfectionOperator infectionOperator = new InfectionOperator();
    
    //class variables for data management
    public Bag myRelations = new Bag();
    public Bag myAgents = new Bag();
    public Bag myInterventions = new Bag();

    //all class constructors:
    public SimpactII(long seed) {
        super(seed); //your run of Simpact II can reseed the RNG
    }

    public SimpactII() {
        this(System.currentTimeMillis()); //... or not reseed RNG
    }

    public SimpactII(int population) {
        //a call to this constructor reseeds RNG and creates "population" number
        //of basic agents
        this();
        addAgents(Agent.class, population);
    }

    //all class methods go here:
    public void start() {
        super.start();

        //reset everything:
        myAgents.clear();       //all agents ever
        myRelations.clear();   //all relations ever
        world.clear();          //geographic placements
        network.clear();        //sexual network

        //add the agents
        this.addPopulations();

        //schedule time operator
        timeOperator.preProcess(this);
        schedule.scheduleRepeating(schedule.EPOCH, 1, timeOperator);

        //schedule infection operator
        infectionOperator.preProcess(this);
        schedule.scheduleRepeating(schedule.EPOCH, 2, infectionOperator);
        
        //anonymous class to schedule a stop
        schedule.scheduleOnce(52 * numberOfYears, new Steppable() {
            public void step(SimState state) {
                state.finish();
            }
        });
    }

    /** Add N agents of type agentClass to the population. A HashMap<String,Object>
     * may be provided if the agent class requires additional arguments. If none 
     * are required or provided, the default constructor will be called.
    */
    public void addAgents(Class agentClass, int N, HashMap<String,Object> attr) {
        if (agentClass.isInstance(Agent.class)) {
            System.err.println("Can only add type Agent to population");
            System.exit(1);
        }
        this.subPopulationTypes.add(agentClass);
        this.subPopulationNum.add(N);
        this.subPopulationArgs.add(attr);
        this.population += N; //add the number of agents to our population
    }
    
    /** Add N agents of type agentClass to the population. 
    */
    public void addAgents(Class agentClass, int number) {        
        addAgents(agentClass, number, new HashMap<String,Object>());
    }
    
    public void addIntervention(Class c, double start, double spend){
        try {
            Agent intervention = (Agent) c.getConstructor(new Class[]{double.class , double.class}).newInstance(start, spend);            
            this.myInterventions.add(intervention);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to add intervention " + c + "\n" + e);
        }        
    }
    public void addIntervention(Intervention i){
        this.myInterventions.add(i);
    }

    private final void addPopulations() {
        //called at the beginning of the simulation to add agents
        int numSubPopulations = subPopulationTypes.size();
        if (numSubPopulations<=0){ 
            addAgents(Agent.class,1000); 
            numSubPopulations = 1;
        }
        for (int i = 0; i < numSubPopulations; i++) {
            Class population = (Class) subPopulationTypes.get(i);
            int number = (int) subPopulationNum.get(i);
            HashMap attr = (HashMap) subPopulationArgs.get(i);
            addNAgents(population, number, attr);
        }
    }

    private final void addNAgents(Class c, int N, HashMap attr) {
        try {
            for (int i = 0; i < N; i++) {
                Agent a = (Agent) c.getConstructor(new Class[]{SimpactII.class, HashMap.class}).newInstance(this, attr);
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to add agent " + c + "\n" + e);
        }
    }
    
    public final void resetPopulations(){
        subPopulationArgs.clear();
        subPopulationNum.clear();
        subPopulationTypes.clear();
        population = 0;
    }

    public void formRelationship(Agent agent1, Agent agent2, double duration) {
        //if the agents didn't have anything to say about how long the relationship should last...
        if (duration == 0) { //(indicates indifference from agents)
            duration = relationshipDurations.nextValue();//...use the next value
        }

        //add the new relationship to the network
        network.addEdge(agent1, agent2, duration);

        //add this relationship to the relation data manager
        myRelations.add(new Relationship(agent1, agent2,
                schedule.getTime(), schedule.getTime() + duration));
    }

    public void dissolveRelationship(Edge e) {
        Agent agent1 = (Agent) e.getFrom();
        agent1.informDissolution();
        Agent agent2 = (Agent) e.getTo();
        agent2.informDissolution();
        network.removeEdge(e);
    }
    public void addAttribute(String key, Object value){
        Bag agents = network.getAllNodes();
        for(int i = 0 ; i < agents.size(); i++){ //add syphilis weeks infected attribute to every one
            Agent agent = (Agent) agents.get(i);
            agent.attributes.put(key, value);
        }
    }

    //graph functions
    public final JFrame agemixingScatter() {
        return new AgeMixingScatter(this);
    }

    public final JFrame demographics() {
        return new Demographics(this);
    }

    public final JFrame formedRelations() {
        return new FormedRelations(this);
    }

    public final JFrame prevalence() {
        return new Prevalence(this);
    }

    //CSV export methods
    public final void writeCSVRelations(String filename) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("maleAge,femaleAge,start,end\n");
            int numRelations = myRelations.size();
            for (int j = 0; j < numRelations; j++) {
                Relationship r = (Relationship) myRelations.get(j);
                if (r.getAgent1().isMale()) {
                    out.write(r.getAgent1().getAge() + "," + r.getAgent2().getAge());
                } else {
                    out.write(r.getAgent2().getAge() + "," + r.getAgent1().getAge());
                }
                out.write(r.getStart() + "," + r.getEnd() + "\n");
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public final void writeCSVPopulation(String filename) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("personID,gender,age,timeOfInfection,timeOfBirth,timeOfDeath\n");
            int numAgents = myAgents.size();
            for (int j = 0; j < numAgents; j++) {
                Agent a = (Agent) myAgents.get(j);
                int wi;
                if (a.weeksInfected <= 0) {
                    wi = 0;
                } else {
                    wi = (int) ((numberOfYears * 52) - a.weeksInfected);
                }

                out.write(a.hashCode() + "," + a.isMale() + "," + a.getAge() + "," + wi
                        + "," + a.getTimeOfAddition() + "," + a.timeOfRemoval + "\n");
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public final void writeCSVEventCounter(String filename) {
        try {
            int now = (int) Math.min(numberOfYears * 52, schedule.getTime()); //determine if we are at the end of the simulation or in the middle
            //initialize array for counts of stuff
            int[] infections = new int[now + 1];
            //add more here when more events are added

            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("timestep,infections\n");
            int numAgents = myAgents.size();
            for (int j = 0; j < numAgents; j++) {
                Agent a = (Agent) myAgents.get(j);

                //add event times to the array
                if (now - a.weeksInfected > 0) {
                    infections[now - a.weeksInfected]++;
                    //add more here when more events are added
                }
            }

            //go through times and write events
            for (int t = 0; t < now; t++) {
                out.write(t + ",");
                out.write(infections[t] + ",");
                //add more here when more events are added

                out.write("\n");
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e);
        }
    }

    //getters and setters / inspectors for the GUI
    public final int getPopulation() {
        return population;
    }

    public final void setPopulation(int val) {
        population = Math.max(0, val);
    }

    //graph possibilities for the GUI
    public final double[] getDNPDistribution() {
        Bag agents = network.getAllNodes();
        double[] distro = new double[agents.numObjs];
        int len = agents.size();
        for (int i = 0; i < len; i++) {
            distro[i] = ((Agent) (agents.get(i))).getDNP();
        }
        return distro;
    }

    public final double[] getPartnerDistribution() {
        Bag agents = network.getAllNodes();
        double[] distro = new double[agents.numObjs];
        int len = agents.size();
        for (int i = 0; i < len; i++) {
            distro[i] = ((Agent) (agents.get(i))).getPartners();
        }
        return distro;
    }

    public final double[] getAgeDistribution() {
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
        doLoop(new MakesSimState() {
            public SimState newInstance(long seed, String[] args) {
                try {
                    return state;//not actually a new instance! it's this instance!
                } 
                catch (Exception e) {
                    throw new RuntimeException("Exception occurred while trying to construct the simulation \n" + e);
                }
            }

            public Class simulationClass() {
                return SimpactII.class;
            }
        }, args);
    }

    public void run() {
        //no args provided        
        run(new String[0]);
    }

    public static void main(String[] args) { //for running from the command line
        System.err.println("***Runing SimpactII with default values");
        doLoop(SimpactII.class, args); //just some necessary java stuff -- running this will run default        
        System.exit(0);
    }
}