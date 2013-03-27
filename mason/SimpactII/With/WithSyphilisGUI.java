/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.SyphilisAgent;
import SimpactII.GUI;
import java.awt.Color;
import sim.display.Console;
import sim.display.GUIState;

/**
 *
 * @author visiting_researcher
 */
public class WithSyphilisGUI extends GUI {
    
    public WithSyphilisGUI(){
        super(new WithSyphilis() );
    }
    @Override
    public Color howDoIDraw(Agent agent){
        Color c = super.howDoIDraw(agent);
        SyphilisAgent sa = (SyphilisAgent) agent;
        if (sa.syphilisWeeksInfected>0 && sa.weeksInfected>0){ //coinfection!
            return Color.ORANGE;
        }else if( sa. syphilisWeeksInfected > 0){
            return Color.CYAN;
        }else{            
            return c;
        }
    }
    public static void main(String[] args){
        WithSyphilisGUI vid = new WithSyphilisGUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }
    
}
