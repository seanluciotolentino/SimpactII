/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.SimpactII;

/**
 *
 * @author visiting_researcher
 */
public class WithDefault {
    
    public static void main(String[] args) { //for running from the command line
        SimpactII s = new SimpactII();
        s.population = 100;
        s.numberOfYears = 5;
        s.run();
        //s.formationScatter();
        s.demographics();
        //System.exit(0);
    }
    
}
