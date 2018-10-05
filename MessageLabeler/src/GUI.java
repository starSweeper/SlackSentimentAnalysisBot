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
    private JPanel labelButtonPanel = new JPanel();
    private JPanel messagePanel = new JPanel();
    private JPanel skipButtonPanel = new JPanel();

    GUI(){
        //Add components to panels
        labelButtonPanel.add(workRelatedButton);
        labelButtonPanel.add(semiWorkRelatedButton);
        labelButtonPanel.add(notWorkRelatedButton);

        skipButtonPanel.add(skipButton);
        messagePanel.add(message);

        window.setFocusable(true);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit the program when the exit button is pressed
        window.setTitle("Message Labeling Interface");

        window.add(labelButtonPanel, BorderLayout.SOUTH);
        window.add(messagePanel, BorderLayout.CENTER);
        window.add(skipButtonPanel, BorderLayout.NORTH);

        window.pack();
        window.setVisible(true);
    }
}
