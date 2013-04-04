package SimpactII.InfectionOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio Tolentino
 * 
 * An infection operator which varies HIV infectivity with weeks infected. This
 * is in line with the literature which indicates that HIV infectivity is very 
 * high at first, then drops down to an asymptomic phase, then ramps up again
 * during the last few months of life. 
 * 
 */
public class PhaseInfectionOperator extends InfectionOperator{
    
    //default class variables about infection
    public int initialNumberInfected = 5;
    public int weeksStage1 = 12; //number of weeks in Primary Infection phase
    public double infectivityStage1 = 0.032;
    public int weeksStage2 = 4*12*8; //4 weeks / month x 12 months / year x 8 years = 384 weeks
    public double infectivityStage2 = 0.0035;
    public int weeksStage3 = 12; 
    public double infectivityStage3 = 0.0152;
    
    public PhaseInfectionOperator(){       
    }
    
    public PhaseInfectionOperator(int[] weekStages, double[] infectivityStages, 
            int initialNumberInfected,SimpactII s){
        //grab infection parameters:
        this.weeksStage1 = weeksStage1; //number of weeks in Primary Infection phase
        this.weeksStage2 = weeksStage2;
        this.weeksStage3 = weeksStage3;
        
        this.infectivityStage1 = infectivityStage1;
        this.infectivityStage2 = infectivityStage2;
        this.infectivityStage3 = infectivityStage3;
    }
    
    public double infectivity(Agent agent) {
        if (agent.weeksInfected < weeksStage1)
            return infectivityStage1;
        else if (agent.weeksInfected < weeksStage2)
            return infectivityStage2;
        else
            return infectivityStage3;        
    }

    
}
