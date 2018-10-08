import javax.swing.*;

public class Message {
    private String messageContent;
    private String sender;
    private JTextArea textArea;

    Message(String author, String content){
        messageContent = content;
        sender = author;
        textArea = new JTextArea(content);
    }

    public String getMessageContent(){
        return messageContent;
    }

    public String getSender(){
        return sender;
    }

    public JTextArea getTextArea(){
        return textArea;
    }

}
