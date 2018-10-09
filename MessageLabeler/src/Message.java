import javax.swing.*;
import java.awt.*;

public class Message {
    private String messageContent;
    private JTextArea textArea;

    Message(String content){
        messageContent = content;
        textArea = new JTextArea(content);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setPreferredSize(new Dimension(800, 300));
        textArea.setEditable(false);
    }

    public String getMessageContent(){
        return messageContent;
    }

    public JTextArea getTextArea(){
        return textArea;
    }

}
