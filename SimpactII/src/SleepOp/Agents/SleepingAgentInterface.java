/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SleepOp.Agents;

/**
 *
 * @author Lucio -- this is the interface for a sleeping agent. Since java doesn't
 * allow double inheritance, I have to solve the diamond problem with an interface.
 * This means that all the Agents have the same code except for which agent type
 * they inherit from.
 */
public interface SleepingAgentInterface {
    
    public boolean getSleeping();    
    public void setSleeping(boolean sleep);
        
}
