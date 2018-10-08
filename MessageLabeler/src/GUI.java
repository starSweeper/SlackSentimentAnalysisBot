//Needed for GUI components
import javax.swing.*;
import javax.swing.JFrame;
import java.awt.*;

//Needed for ArrayList
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
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
    private JPanel messageCards = new JPanel(new CardLayout());

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
                removeMessage();
                if((slackMessages.indexOf(currentMessage) + 1) >= slackMessages.size()){
                    outputData();
                }
                else {
                    displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));
                }
            }
        });
        workRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if((slackMessages.indexOf(currentMessage) + 1) >= slackMessages.size()){
                    outputData();
                }
                else {
                    addToWordCount(currentMessage, workRelatedWords);
                    removeMessage();
                    displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));
                }

            }
        });
        semiWorkRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if((slackMessages.indexOf(currentMessage) + 1) >= slackMessages.size()){
                    outputData();
                }
                else {
                    addToWordCount(currentMessage, semiWorkRelatedWords);
                    removeMessage();
                    displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));
                }
            }
        });
        notWorkRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                if((slackMessages.indexOf(currentMessage) + 1) >= slackMessages.size()){
                    outputData();
                }
                else {
                    addToWordCount(currentMessage, nonWorkRelatedWords);
                    removeMessage();
                    displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));
                }
            }
        });

        displayMessage(slackMessages.get(0));

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

    private void displayMessage(Message messageToDisplay){
        currentMessage = messageToDisplay;
        messagePanel.add(currentMessage.getTextArea());
        messagePanel.revalidate();
    }

    private void removeMessage(){
        messagePanel.remove(currentMessage.getTextArea());
    }

    private void addToWordCount(Message message, HashMap<String,Integer> map){
        String[] wordsFound = message.getMessageContent().split(" ");
        for(int i = 0; i < wordsFound.length; i++){
            map.put(wordsFound[i], map.getOrDefault(wordsFound[i], 0) + 1);
        }
    }

    private void outputData(){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("messageData.txt"));
            //bufferedWriter.write("test");
            for(String word: workRelatedWords.keySet()){
                bufferedWriter.write(word + " " + workRelatedWords.get(word).toString() + "\n");
            }
            bufferedWriter.close();
        }catch(Exception e){
            System.out.println("Something went wrong when trying to read messages.txt");
        }
    }
}
