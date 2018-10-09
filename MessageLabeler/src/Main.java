import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Files; //Needed to read from/write to files
import java.nio.file.Paths; //Needed to read from/write to files
import java.util.ArrayList;

public class Main {
    public static void main(String[] args){
        //Generate Graphical User Interface
        GUI labelMachine = new GUI(readFile("messages.txt"));
    }

    public static ArrayList readFile(String filename){
        ArrayList<Message> slackMessages = new ArrayList<>();
        try{
            FileInputStream messageList = new FileInputStream(filename);
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(messageList));
            String newLine;
            while((newLine = buffRead.readLine()) != null){
                Message newMessage = new Message(newLine);
                slackMessages.add(newMessage);
            }
        }
        catch (FileNotFoundException e404){
            return readFile("MessageLabeler/messages.txt");
        }
        catch (Exception e){
            System.out.println("Something went wrong when trying to read messages.txt");
            System.out.println(e.getMessage());
        }
        return slackMessages;
    }
}
