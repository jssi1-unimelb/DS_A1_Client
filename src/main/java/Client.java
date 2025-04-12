import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Client implements EventPublisher {
    private Socket client;
    protected DataInputStream dis;
    private DataOutputStream dos;
    private boolean liveConnection = false;
    private ServerReader serverReader = null;
    private EventListener guiListener;
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

            return switch (command) {
                case "meaning", "remove" -> parameters.length == 1;
                case "add_meaning" -> parameters.length == 2;
                case "update" -> parameters.length == 3;
                case "new" -> parameters.length >= 2; // At least 2 parameters
                default -> true;
            };
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
                    }
                }
            }
        } catch (IOException e) {
            notifyListener("error: an IO error occurred");
        }
    }

    // Close everything
    public synchronized void closeConnection() {
       try {
           liveConnection = false;
           dis.close();
           dos.close();
           client.close();
           wait();
       } catch (IOException | InterruptedException e) {
           throw new RuntimeException(e);
       }
    }

    public void startConnection() {
        // Open your connection to a server, at port 1234
        String host = "127.0.0.1";
        try {
            client = new Socket(host,1234);
            liveConnection = true;

            // Open input and output streams
            dis = new DataInputStream(client.getInputStream());
            dos = new DataOutputStream(client.getOutputStream());

            if(serverReader == null) { // Hasn't been instantiated yet
                serverReader = new ServerReader(this);
                serverReader.start();
            } else {
                notifyAll(); // Wake up existing thread
            }
            notifyListener("Connection established");
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
        this.guiListener = listener;
    }

    @Override
    public void notifyListener(String msg) {
        guiListener.onEvent(msg);
    }
}