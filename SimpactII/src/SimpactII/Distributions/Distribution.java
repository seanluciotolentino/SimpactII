/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.Distributions;

import java.io.Serializable;

/**
 *
 * @author visiting_researcher
 */
public interface Distribution extends Serializable{
    public double nextValue();    
}
