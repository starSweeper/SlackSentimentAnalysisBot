//Needed for GUI components
import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;

//Needed for ArrayList
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class GUI {

    private HashMap<String,Integer> workRelatedWords = new HashMap<>();
    private HashMap<String,Integer> semiWorkRelatedWords = new HashMap<>();
    private HashMap<String,Integer> nonWorkRelatedWords = new HashMap<>();

    private JFrame window = new JFrame(); //Holds all GUI components

    //Buttons for labeling
    private JButton skipButton = new JButton("SKIP");
    private JButton workRelatedButton = new JButton("WORK RELATED");
    private JButton semiWorkRelatedButton = new JButton("SEMI WORK RELATED");
    private JButton notWorkRelatedButton = new JButton("NOT WORK RELATED");
    private ArrayList<JPanel> messages = new ArrayList<>();
    private JPanel labelButtonPanel = new JPanel();
    private JPanel messagePanel = new JPanel();
    private JPanel skipButtonPanel = new JPanel();
    private ArrayList<Message> slackMessages;
    private Message currentMessage;

    GUI(ArrayList<Message> messageList){
        slackMessages = messageList;

        //Add components to panels
        labelButtonPanel.add(workRelatedButton);
        labelButtonPanel.add(semiWorkRelatedButton);
        labelButtonPanel.add(notWorkRelatedButton);
        skipButtonPanel.add(skipButton);
        messagePanel.setLayout(new CardLayout());

        skipButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

            }
        });
        workRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

            }
        });
        semiWorkRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

            }
        });
        notWorkRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

            }
        });

        for(int i = 0; i  < slackMessages.size(); i++){
            currentMessage = slackMessages.get(i);


        }


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
