/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Agents;

import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class AgeAgent extends Agent{
    
    private double band;
    private double offset;
    
    public AgeAgent(SimpactII state, double bandwidth, double offset){
        super(state);        
        this.band = bandwidth;
        this.offset = offset;
    }
    
    public AgeAgent(SimpactII state, double bandwidth, double offset, int DefaultAge){
        super(state);        
        this.band = bandwidth;
        this.offset = offset;
        this.age = DefaultAge;
    }
    
    public boolean isDesirable(Agent other){

        boolean ageIsRight;
        if ( isMale() )
            ageIsRight = (getAge() - other.getAge() ) < offset + band && (getAge() - other.getAge() ) > offset - band ; 
        else
            ageIsRight = (other.getAge() - getAge() ) < offset + band && (other.getAge() - getAge() ) > offset - band ; 
        
        //if (isMale() && age>28 && age < 30 && ageIsRight && other.isLookingFor(this) ) //DEBUG
        //    System.out.println(toString() );
        return ageIsRight && other.isLookingFor(this);
    }
    
    public boolean isLookingFor(Agent other){
        return getPartners() < getDNP() && (isMale() ^ other.isMale());
    }
    
    
    
}
