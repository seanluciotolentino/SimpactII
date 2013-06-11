package SimpactII;

import SimpactII.Agents.Agent;
import SimpactII.DataStructures.Relationship;
import SimpactII.Distributions.*;
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
    public double numberOfYears = 10;
    public Distribution degrees = new PowerLawDistribution(8,3.0,this.random);
    public Distribution ages = new UniformDistribution(15.0, 65.0,this.random);//new PowerLawDistribution(1.0,3);
    public Distribution relationshipDurations = new UniformDistribution(1.0, 10.0, this.random);//new BetaDistribution(0.1,0.9);
    //class variables for main operations
    public TimeOperator timeOperator = new TimeOperator();
    public InfectionOperator infectionOperator = new InfectionOperator();
    //class variables for data management
    public Bag myRelations = new Bag();
    public Bag myAgents = new Bag();
    public Bag myInterventions = new Bag();

    //ALL CLASS CONSTRUCTORS
    /**
     * Default constructor: Reseeds the random number generator with the current
     * time of the system and does not add any agents (this allows a user to
     * specify agents to add – if no agents are added to the simulation, 1000
     * basic agents are added).
     */
    public SimpactII() {
        this(System.currentTimeMillis()); //... or not reseed RNG
    }

    /**
     * Reseeds the random number generator with <b>seed</b>.
     */
    public SimpactII(long seed) {
        super(seed); //your run of Simpact II can reseed the RNG
    }

    /**
     * Adds <b>population</b> number of Agents (basic agents)
     */
    public SimpactII(int population) {
        //a call to this constructor reseeds RNG and creates "population" number
        //of basic agents
        this();
        addAgents(Agent.class, population);
    }

    /**
     * Reseeds the random number generator with <b>seed</b> and adds
     * <b>population</b> number of Agents (basic agents)
     */
    public SimpactII(long seed, int population) {
        this(seed); //your run of Simpact II can reseed the RNG
        addAgents(Agent.class, population);
    }

    //ALL CLASS METHODS GO BELOW
    /**
     * This method is called by the SimState main loop. To run the simulation,
     * instead use the <b>run</b> method.
     */
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

    /**
     * Add N agents of type agentClass to the population. A
     * HashMap<String,Object>
     * may be provided if the agent class requires additional arguments.
     */
    public void addAgents(Class agentClass, int N, HashMap<String, Object> attr) {
        if (agentClass.isInstance(Agent.class)) {
            System.err.println("Can only add type Agent to population");
            System.exit(1);
        }
        this.subPopulationTypes.add(agentClass);
        this.subPopulationNum.add(N);
        this.subPopulationArgs.add(attr.clone());
        this.population += N; //add the number of agents to our population
    }

    /**
     * Add N agents of type agentClass to the population.
     */
    public void addAgents(Class agentClass, int number) {
        addAgents(agentClass, number, new HashMap<String, Object>());
    }

    /**
     * Add an new intervention of type C with start time <b>start</b> and
     * spending amount <b>spend</b>.
     */
    public void addIntervention(Class c, double start, double spend) {
        try {
            Intervention intervention = (Intervention) c.getConstructor(new Class[]{double.class, double.class}).newInstance(start, spend);
            this.myInterventions.add(intervention);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while trying to add intervention " + c + "\n" + e);
        }
    }

    /**
     * Add an intervention <b>i</b> that has already been initialized.
     */
    public void addIntervention(Intervention i) {
        this.myInterventions.add(i);
    }

    /**
     * Remove all the added agents from the lists.
     */
    public final void resetPopulations() {
        subPopulationArgs.clear();
        subPopulationNum.clear();
        subPopulationTypes.clear();
        population = 0;
    }

    /**
     * Adds an edge to the network between <b>agent1</b> and <b>agent2</b>. If
     * <b>duration</b> is 0, the next value from the relationship distribution
     * will be drawn.
     */
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
    
    /**
     * Removes the relation represented by the edge <b>e</b> from the network. 
     * Both acting agents in the relationship are informed so that each are able
     * to update their own number of partners counters.
     */
    public void dissolveRelationship(Edge e) {
        Agent agent1 = (Agent) e.getFrom();
        agent1.informDissolution();
        Agent agent2 = (Agent) e.getTo();
        agent2.informDissolution();
        network.removeEdge(e);
    }

    /**
     * Adds the attribute <b>key</b> to every agent with the values
     * <b>value</b>.
     */
    public void addAttribute(String key, Object value) {
        Bag agents = network.getAllNodes();
        for (int i = 0; i < agents.size(); i++) { //add syphilis weeks infected attribute to every one
            Agent agent = (Agent) agents.get(i);
            agent.attributes.put(key, value);
        }
    }

    /**
     * Launch the GUI interpretation of this model.
     */
    public void launchGUI() {
        new GUI(this);
    }

    //PRIVATE METHODS 
    private final void addPopulations() {
        //called at the beginning of the simulation to add agents
        int numSubPopulations = subPopulationTypes.size();
        if (numSubPopulations <= 0) {
            addAgents(Agent.class, 1000);
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

    //GRAPHING METHODS
    /**
     * Generate the age mixing scatter plot. This is a scatter of all
     * relationships, with age of male along the y-axis, and age of the female
     * along the x-axis.
     */
    public final JFrame agemixingScatter() {
        return new AgeMixingScatter(this);
    }

    /**
     * Generate the demographics plot. This is a series of stacked bar charts
     * denoting the distribution of ages at different time steps of the model.
     */
    public final JFrame demographics() {
        return new Demographics(this);
    }

    /**
     * Generate the formed relations plot. This is a visual representation of
     * the duration of relationships and the number of relationships at any
     * given time.
     */
    public final JFrame formedRelations() {
        return new FormedRelations(this);
    }

    /**
     * Generate the prevalence plot. This is a time series plot depicting
     * prevalence of HIV, stratified by gender, over time. Incidence (raw number
     * of cases each week) is displayed in a second graph.
     */
    public final JFrame prevalence() {
        return new Prevalence(this);
    }

    //CSV EXPORT METHODS
    /**
     * Generate a comma separated values (csv) file of all relationships which
     * occurred in the simulation. Heading is
     * 
     * maleAge,femaleAge,start,end
     */
    public final void writeCSVRelations(String filename) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("male,female,start,end\n");
            int numRelations = myRelations.size();
            for (int j = 0; j < numRelations; j++) {
                Relationship r = (Relationship) myRelations.get(j);
                if (r.getAgent1().isMale()) {
                    out.write(r.getAgent1().hashCode()+ "," + r.getAgent2().hashCode());
                } else {
                    out.write(r.getAgent2().hashCode() + "," + r.getAgent1().hashCode());
                }
                out.write("," + r.getStart() + "," + r.getEnd() + "\n");
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Generate a comma separated values (csv) file of the details of
     * individuals within the population. Heading is:
     * 
     * personID,gender,age,timeOfInfection,timeOfBirth,timeOfDeath
     */
    public final void writeCSVPopulation(String filename) {
        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("personID,gender,age,timeOfInfection,timeOfBirth,timeOfDeath,attributes\n");
            int now = (int) Math.min(this.schedule.getTime(), numberOfYears * 52);
            int numAgents = myAgents.size();
            for (int j = 0; j < numAgents; j++) {
                Agent a = (Agent) myAgents.get(j);
                int wi;
                if (a.weeksInfected <= 0) {
                    wi = 0;
                } else {
                    wi = (int) (now - a.weeksInfected);
                }

                out.write(a.hashCode() + "," + a.isMale() + "," + a.getAge() + "," + wi
                        + "," + a.getTimeOfAddition() + "," + a.timeOfRemoval + "," +
                        a.attributes + "\n");
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Generate a comma separated values (csv) file of the time at which events
     * occurred in the simulation. Currently the only event is infection, but
     * will later include intervention events as well.Heading is:
     * 
     * timestep,infections
     */
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

    //GETTERS AND SETTERS (for the GUI)
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
                } catch (Exception e) {
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
        SimpactII s = new SimpactII();
        s.run();
        System.exit(0);
    }

    /**
     * @return the subPopulationTypes
     */
    public Bag getSubPopulationTypes() {
        return subPopulationTypes;
    }

    /**
     * @return the subPopulationNum
     */
    public Bag getSubPopulationNum() {
        return subPopulationNum;
    }

    /**
     * @return the subPopulationArgs
     */
    public Bag getSubPopulationArgs() {
        return subPopulationArgs;
    }
}