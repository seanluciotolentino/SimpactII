/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.InfectionOperators.PhaseInfectionOperator;
import SimpactII.InfectionOperators.LDInfectionOperator;
import SimpactII.SimpactII;
import static sim.engine.SimState.doLoop;

/**
 *
 * @author visiting_researcher
 */
public class WithLDInfectionOperator extends SimpactII{
    
   
    public WithLDInfectionOperator(){
        super(System.currentTimeMillis());
    }
    
    public void addInfectionOperator(){
        schedule.scheduleRepeating(schedule.EPOCH, 2, new LDInfectionOperator(this) );
    }

}
