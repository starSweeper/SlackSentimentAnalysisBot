import javax.swing.*;

public class Message {
    private String messageContent;
    private JTextArea textArea;

    Message(String content){
        messageContent = content;
        textArea = new JTextArea(content);
    }

    public String getMessageContent(){
        return messageContent;
    }

    public JTextArea getTextArea(){
        return textArea;
    }

}
