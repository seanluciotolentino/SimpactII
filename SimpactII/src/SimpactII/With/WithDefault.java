package SimpactII.With;

import SimpactII.Agents.*;
import SimpactII.InfectionOperators.*;
import SimpactII.Interventions.*;
import SimpactII.SimpactII;

/**
 *
 * @author Lucio Tolentino
 * 
 * One example of how you can run the basic version of SimpactII with some 
 * customizable values.  This class does not extend the SimpactII class (which 
 * required if you want to override methods), but instead only has a single main
 * method with sets the various parameters of the model. Note that we can use 
 * different time and infection operators without extending SimpactII, but we 
 * cannot use different agents. After we allow the model to run, we display some
 * graphs.
 * 
 */
public class WithDefault {
    
    public static void main(String[] args) { //for running from the command line
        SimpactII s = new SimpactII();
        s.addAgents(Agent.class, 1000);
        s.numberOfYears = 50;
        //s.addIntervention(HIVTestAndCounsel.class, 4.9, 2000);
        //s.addIntervention(ARVTreatment.class, 5, 1000);
        s.run();
//        s.agemixingScatter();
//        s.demographics();
        s.prevalence();
//        s.formedRelations();
        //s.writeCSVEventCounter("eventfile.csv");
        //System.exit(0);
    }
    
}
