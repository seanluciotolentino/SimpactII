package SleepOp;

import CombinationPrevention.ValidatedModel;
import SimpactII.Agents.Agent;
import SimpactII.Agents.TriAgeAgent;
import SimpactII.Distributions.Normal;
import SimpactII.Distributions.UniformDistribution;
import SimpactII.GUI;
import SimpactII.InfectionOperators.AIDSDeathInfectionOperator;
import SimpactII.InfectionOperators.InterventionInfectionOperator;
import SleepOp.Agents.*;
import SleepOp.Agents.SleepingAgentInterface;
import SleepOp.Operators.*;
import java.awt.Color;
import java.util.HashMap;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author Lucio
 */
public class SleepingValidatedModel extends ValidatedModel{
    
    public SleepingValidatedModel(int population){
        super(population); 
        
        //add "sleeping" time operator
        timeOperator = new SleepingDemographicTimeOperator();
        
        //add "sleeping" infection operator
        AIDSDeathInfectionOperator io = new SleepingAIDSDeathInfectionOperator() ;
            io.transmissionProbability = 0.008;
            io.initialNumberInfected = 5;
            io.HIVIntroductionTime = 3.5*52;//4*52;
            io.CD4AtInfection = new Normal(1500, 250, random);
            io.CD4AtDeath = new UniformDistribution(0, 100, random);
        infectionOperator = new InterventionInfectionOperator(io);

    }

    protected void addHeterogeneousAgents(int population){
        //add "normal" agents
        //male cone agents
        HashMap attributes = new HashMap<String,Object>();
        attributes.put("preferredAgeDifference",0.9);
        attributes.put("probabilityMultiplier",-0.1);
        attributes.put("preferredAgeDifferenceGrowth",0.05);
        attributes.put("adDispersion",0.004);
        attributes.put("genderRatio", 1.0);
        attributes.put("meanAgeFactor", -0.08);
        attributes.put("warmUp",5);
        addAgents(ConeSleepingAgent.class, (int) ((population/2) * 0.5),attributes);
                        
        addAgents(SleepingAgent.class,(int) ((population/2) * 0.5),attributes); //50 men all over the place
        
        //half female band agents non-AD
        attributes.clear();
        attributes.put("preferredAgeDifference",2.0);
        attributes.put("probabilityMultiplier",-0.9);
        attributes.put("preferredAgeDifferenceGrowth",0.01);
        attributes.put("genderRatio", 0.0);
        attributes.put("warmUp",5);
        addAgents(TriSleepingAgent.class,population/4,attributes);
        
        //other half of female agent AD forming
        attributes.put("preferredAgeDifference",2.0);
        attributes.put("probabilityMultiplier",-0.5);
        attributes.put("preferredAgeDifferenceGrowth",0.01);
        addAgents(TriSleepingAgent.class,population/4,attributes);
    }
    
    public void launchGUI(){
        //GUI
        new GUI(this){
            public Color howDoIDraw(Agent agent){
                SleepingAgentInterface sagent = (SleepingAgentInterface) agent;
                if (agent.weeksInfected > 0) {
                    return Color.GREEN;
                }
                if (sagent.getSleeping() ) {
                    return Color.blue;
                } else {
                    return Color.red;
                }
            }
        };
    }
    
    public void dissolveRelationship(Edge e) {
        super.dissolveRelationship(e);
        //after dissolving relationship, sleep someone
        tryToSleep((SleepingAgentInterface) e.getFrom());
        tryToSleep((SleepingAgentInterface) e.getTo());
    }
    
    public void formRelationship(Agent agent1, Agent agent2, double duration) {
        super.formRelationship(agent1, agent2, duration);
        //wake up partners of infected
        if(agent1.weeksInfected>=1)
            ((SleepingAgentInterface) agent2).setSleeping(false);
        if(agent2.weeksInfected>=1)
            ((SleepingAgentInterface) agent1).setSleeping(false);
    }
    
    private void tryToSleep(SleepingAgentInterface a){
        //quickly check to verify we want to sleep this person
        if(infectionOperator.HIVIntroductionTime>schedule.getTime())
            return; //this is super hacky and is inappropriate for an optimization
        if(((Agent)a).weeksInfected > 0)
            return;
        
        Bag relations = network.getEdges(a, new Bag() );
        for(int j = 0 ; j < relations.size(); j++){
            Edge e = (Edge) relations.get(j);
            Agent other = (Agent) e.getOtherNode(a);
            if(other.weeksInfected>0)
                return;
        }
        
        //if you've made it here, you should be slept
        a.setSleeping(true);
    }

    
}
