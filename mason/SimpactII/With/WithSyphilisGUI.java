package SimpactII.With;

import SimpactII.Agents.Agent;
import SimpactII.Agents.SyphilisAgent;
import SimpactII.GUI;
import java.awt.Color;
import sim.display.Console;
import sim.display.GUIState;

/**
 *
 * @author Lucio Tolentino
 * 
 * An example of how to write a GUI for a particular model. In this case the model
 * that included syphilis.  To do this we extend the GUI class, and call the 
 * super constructor with the model we want to use (WithSyphilis).  We then 
 * override the "howDoIDraw" method which tells the GUI how to color nodes. In our
 * example orange for co-infection, cyan for syphilis infection, and default for 
 * other (green for HIV, blue for non-infected male, red for non-infected female).
 * We then implement a main method with is called when we run this file. 
 * 
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
