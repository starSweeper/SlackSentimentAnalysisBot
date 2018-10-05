//Needed for GUI components
import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;

//Needed for ArrayList
import java.util.ArrayList;

public class GUI {
    private JFrame window = new JFrame(); //Holds all GUI components

    //Buttons for labeling
    private JButton skipButton = new JButton("SKIP");
    private JButton workRelatedButton = new JButton("WORK RELATED");
    private JButton semiWorkRelatedButton = new JButton("SEMI WORK RELATED");
    private JButton notWorkRelatedButton = new JButton("NOT WORK RELATED");
    private JTextArea message = new JTextArea();

    GUI(){
        window.setFocusable(true);
        //window.requestFocusInWindow();
        window.setExtendedState(Frame.MAXIMIZED_BOTH);
        //window.setLayout(new CardLayout()); //Allows easy change of components
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit the program when the exit button is pressed
        window.setTitle("Message Labeling Interface");

        window.add(skipButton);
        window.add(workRelatedButton);
        window.add(semiWorkRelatedButton);
        window.add(message);
        window.add(notWorkRelatedButton);

        window.setVisible(true);
    }
}
