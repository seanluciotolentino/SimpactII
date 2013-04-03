/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.InfectionOperators;

import SimpactII.Agents.Agent;
import SimpactII.SimpactII;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 *
 * @author visiting_researcher
 */
public class SyphilisInfectionOperator extends InfectionOperator {
    
    public double syphilisInfectivity;
    public int intialNumberSyphilisInfected;
    
    public SyphilisInfectionOperator(){
        this(0.3,5);
    }
    public SyphilisInfectionOperator(double syphilisInfectivity, int intialNumberSyphilisInfected){
        this.syphilisInfectivity = syphilisInfectivity;
        this.intialNumberSyphilisInfected = intialNumberSyphilisInfected;
    }
    
    public void infectionStep(Agent agent, SimpactII state) {
        super.infectionStep(agent, state); //infect HIV AND also Syphilis
        int syphilisWeeksInfected = (int) agent.attributes.get("SyphilisInfection");
        if (syphilisWeeksInfected >= 1) {
            agent.attributes.put("SyphilisInfection", syphilisWeeksInfected + 1);

            Bag partners = state.network.getEdges(agent, new Bag());
            for (int j = 0; j < partners.size(); j++) {
                Edge relationship = (Edge) partners.get(j);
                Agent partner = (Agent) relationship.getOtherNode(agent);
                int wi = (int) partner.attributes.get("SyphilisInfection");
                if (wi <= 0 && state.random.nextDouble() < syphilisInfectivity) {
                    partner.attributes.put("SyphilisInfector",agent);
                    partner.attributes.put("SyphilisInfection", 1);
                }
            } //partners for loop
        } //if agent infected
    }
    
    public void preProcess(SimpactII state){
        super.preProcess(state);
        addCharacteristics(state);
        performInitialInfections(state);
    }
    private void addCharacteristics(SimpactII state) {
        Bag agents = state.network.getAllNodes();
        for(int i = 0 ; i < agents.size(); i++){ //add syphilis weeks infected attribute to every one
            Agent agent = (Agent) agents.get(i);
            agent.attributes.put("SyphilisInfection", 0);
        }
    }

    private void performInitialInfections(SimpactII state) {
        int pop = state.getPopulation();
        for (int i = 0; i < intialNumberSyphilisInfected; i++) {
            Agent agent = (Agent) state.myAgents.get(state.random.nextInt(pop));
            agent.attributes.put("SyphilisInfection", 1) ;
        }
    }
}
