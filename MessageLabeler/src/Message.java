public class Message {
    private String messageContent;
    private String sender;

    Message(String content, String author){
        messageContent = content;
        sender = author;
    }

    private String getMessageContent(){
        return messageContent;
    }

    private String getSender(){
        return sender;
    }
}
