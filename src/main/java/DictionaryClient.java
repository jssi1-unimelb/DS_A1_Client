// Jiachen Si 1085839
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DictionaryClient implements EventPublisher {
    private Socket client;
    protected DataInputStream dis;
    private DataOutputStream dos;
    private boolean liveConnection = false;
    private ServerReader serverReader = null;
    private EventListener guiListener;
    private final String host;
    private final int port;

    public DictionaryClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Request createRequest(String request) {
        try {
            int index = request.indexOf("("); // Find first instance of (
            if(index == -1) {
                notifyListener("Error: " + request + " is an invalid command");
                return null;
            }

            String command = request.substring(0, index);
            int endIndex = request.indexOf(")");
            String parameterString = request.substring(index+1, endIndex);
            String[] parameters = new String[0];
            if(!parameterString.isEmpty()) {
                parameters = parameterString.split(",");
                parameters =
                        Arrays.stream(parameters)
                                .map(String::trim)
                                .map(s -> s.replaceAll("\"", "")) // Remove quotes
                                .toArray(String[]::new);
            }

            ArrayList<String> validCommands = new ArrayList<>();
            validCommands.add("meaning");
            validCommands.add("remove");
            validCommands.add("add_meaning");
            validCommands.add("update");
            validCommands.add("new");
            validCommands.add("start");
            validCommands.add("exit");
            if(!validCommands.contains(command)) {
                notifyListener("Error: " + request + " is an invalid command");
                return null;
            }

            // Check if command is valid
            boolean isValid = switch (command) {
                case "meaning", "remove" -> parameters.length == 1;
                case "add_meaning" -> parameters.length == 2;
                case "update" -> parameters.length == 3;
                case "new" -> parameters.length >= 2;
                case "start", "exit" -> parameters.length == 0;
                default -> false;
            };

            if(isValid) {
                return new Request(command, parameters);
            }
            notifyListener("Error: incorrect number of parameters were provided");
            return null;
        } catch (IndexOutOfBoundsException e) {
            notifyListener("Error: " + request + " is an invalid command");
            return null;
        }
    }

    public void sendRequest(String command) {
        Request request = createRequest(command);

        if(request == null) {
            return;
        }

        try {
            if(!liveConnection) {
                if(request.command.equals("start")) {
                    startConnection(); // Start up the connection
                } else {
                    notifyListener("Error: cannot fulfil request, not connected to a server");
                }
            } else {
                if(request.command.equals("start")) {
                    notifyListener("Error: connection already established");
                } else {
                    String requestJson = GsonUtil.gson.toJson(request);
                    dos.writeUTF(requestJson); // Send request
                }
            }
        } catch (IOException e) {
            notifyListener("Error: an IO error occurred");
        }
    }

    // Close everything
    public void closeConnection() {
       try {
           liveConnection = false;
           dis.close();
           dos.close();
           client.close();
       } catch (IOException e) {
           notifyListener("Error: an IO error occurred");
       }
    }

    // Pause the server listener
    public synchronized void pauseListener () {
        try {
            wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void startConnection() {
        // Open your connection to a server, at port 1234
        try {
            client = new Socket(host,port);
            liveConnection = true;

            // Open input and output streams
            dis = new DataInputStream(client.getInputStream());
            dos = new DataOutputStream(client.getOutputStream());

            notifyListener("Attempting to connect...");
            if(serverReader == null) { // Hasn't been instantiated yet
                serverReader = new ServerReader(this);
                serverReader.start();
            } else {
                notifyAll(); // Wake up existing thread
            }

        } catch(UnknownHostException uhe) { // Tried connecting to a server that doesn't exist
            notifyListener("Error: cannot connect, server does not exist");
            liveConnection = false;
        } catch(IOException ioe) {
            notifyListener("Error: connection refused, please try again later");
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