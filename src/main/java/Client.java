import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client implements EventPublisher {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private boolean liveConnection = false;
    private EventListener listener;
    ArrayList<String> validCommands;

    public Client() {
        this.validCommands = new ArrayList<>();
        validCommands.add("start");
        validCommands.add("exit");
        validCommands.add("meaning");
        validCommands.add("new");
        validCommands.add("remove");
        validCommands.add("add_meaning");
        validCommands.add("update");
    }

    public boolean validateRequest(String request) {
        int index = request.indexOf("("); // Find first instance of (
        if(index == -1) {
            return false;
        }

        String command = request.substring(0, index);
        if(validCommands.contains(command)) { // Command keyword is valid
            int endIndex = request.indexOf(")");
            String parameterString = request.substring(index+1, endIndex);
            String[] parameters = parameterString.split(",");

            switch (command) {
                case "meaning":
                case "remove":
                    return parameters.length == 1;
                case "add_meaning":
                    return parameters.length == 2;
                case "update":
                    return parameters.length == 3;
                case "new":
                    return parameters.length >= 2; // At least 2 parameters
                default:
                    return true;
            }
        }
        return false;
    }

    public void sendRequest(String request) {
        if(!validateRequest(request)) {
            notifyListener(request + " is an invalid command");
            return;
        }

        try {
            if(!liveConnection) {
                if(request.equals("start()")) {
                    startConnection(); // Start up the connection
                } else {
                    notifyListener("cannot fulfil request, not connected to a server");
                }
            } else {
                if(request.equals("start()")) {
                    notifyListener("connection already established");
                } else {
                    dos.writeUTF(request); // Send request
                    if(request.equals("exit()")) {
                        closeConnection();
                        notifyListener("connection closed");
                    } else {
                        String response = dis.readUTF(); // Block and listen for server response
                        notifyListener(response);
                    }
                }
            }
        } catch (IOException e) {
            notifyListener("error: an IO error occurred");
        }
    }

    // Close everything
    public void closeConnection() {
       try {
           liveConnection = false;
           dis.close();
           is.close();
           dos.close();
           os.close();
           socket.close();
       } catch (IOException e) {
           throw new RuntimeException(e);
       }

    }

    public void startConnection() {
        // Open your connection to a server, at port 1234
        String host = "127.0.0.1";
        try {
            Socket client = new Socket(host,1234);
            liveConnection = true;

            // Open input and output streams
            is = client.getInputStream();
            dis = new DataInputStream(is);
            os = client.getOutputStream();
            dos = new DataOutputStream(os);
            notifyListener("Connection Established");
        } catch(UnknownHostException uhe) { // Tried connecting to a server that doesn't exist
            System.out.println("Unknown host: " + host);
            notifyListener("cannot connect, server does not exist");
            liveConnection = false;
        } catch(IOException ioe) {
            System.out.println("IOException: " + ioe);
            notifyListener("connection refused, please try again later");
            liveConnection = false;
        }
    }

    @Override
    public void addListener(EventListener listener) {
        this.listener = listener;
    }

    @Override
    public void notifyListener(String msg) {
        listener.onEvent(msg);
    }
}