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
    private ArrayList<Integer> labels = new ArrayList<>();
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
                displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));

                labels.add(-1);
                if((slackMessages.indexOf(currentMessage) + 1) == slackMessages.size()){
                    outputData();
                }
            }
        });
        workRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                labels.add(1);
                addToWordCount(currentMessage, 1);
                removeMessage();

                if((slackMessages.indexOf(currentMessage) + 1) == slackMessages.size()){
                    outputData();
                    return;
                }

                displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));
            }
        });
        semiWorkRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                labels.add(2);
                addToWordCount(currentMessage, 2);
                removeMessage();

                if((slackMessages.indexOf(currentMessage) + 1) == slackMessages.size()){
                    outputData();
                    return;
                }

                displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));
            }
        });
        notWorkRelatedButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                labels.add(3);
                removeMessage();
                addToWordCount(currentMessage, 3);

                if((slackMessages.indexOf(currentMessage) + 1) == slackMessages.size()){
                    outputData();
                    return;
                }
                displayMessage(slackMessages.get(slackMessages.indexOf(currentMessage) + 1));
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
        window.setPreferredSize(new Dimension(800, 300));
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

    private void addToWordCount(Message message, int whichMap){
        String[] wordsFound = message.getMessageContent().replaceAll("[^A-Za-z0-9 ]", "").split(" ");
        int workAdd = 0;
        int semiWorkAdd = 0;
        int nonWorkAdd = 0;

        if(whichMap == 1){
            workAdd = 1;
        }
        else if(whichMap == 2){
            semiWorkAdd = 1;
        }
        else{
            nonWorkAdd = 1;
        }


        for(int i = 0; i < wordsFound.length; i++){
            workRelatedWords.put(wordsFound[i], workRelatedWords.getOrDefault(wordsFound[i], 0) + workAdd);
            semiWorkRelatedWords.put(wordsFound[i], semiWorkRelatedWords.getOrDefault(wordsFound[i], 0) + semiWorkAdd);
            nonWorkRelatedWords.put(wordsFound[i], nonWorkRelatedWords.getOrDefault(wordsFound[i], 0) + nonWorkAdd);
        }
    }

    private void createLabelsFile(){
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("labels.txt"));
            for(int label : labels){
                bufferedWriter.write(label + "\n");
            }
            bufferedWriter.close();
        }catch(Exception e){
            System.out.println("Something went wrong when trying to read labels.txt");
        }
    }

    private void outputData(){
        createLabelsFile();
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("messageData.txt"));
            //bufferedWriter.write("test");
            for(String word: workRelatedWords.keySet()){
                bufferedWriter.write(word + " " + workRelatedWords.get(word).toString() + " " +
                        semiWorkRelatedWords.get(word).toString() + " "
                        + nonWorkRelatedWords.get(word).toString() + "\n");
            }
            bufferedWriter.close();
        }catch(Exception e){
            System.out.println("Something went wrong when trying to read messages.txt");
        }

        removeMessage();
        JTextArea allDoneMessage = new JTextArea("Nothing left for you to label. Feel free to exit!");
        allDoneMessage.setEditable(false);
        allDoneMessage.setPreferredSize(new Dimension(800, 300));
        allDoneMessage.setLineWrap(true);
        allDoneMessage.setWrapStyleWord(true);
        messagePanel.add(allDoneMessage);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messagePanel.revalidate();
        skipButton.setEnabled(false);
        workRelatedButton.setEnabled(false);
        semiWorkRelatedButton.setEnabled(false);
        notWorkRelatedButton.setEnabled(false);
    }
}
