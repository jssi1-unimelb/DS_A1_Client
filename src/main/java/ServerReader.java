
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

// Separates the listening blocking activity to its own thread
public class ServerReader extends Thread {
    private final Client client;

    public ServerReader(Client client) {
        this.client = client;
    }

    public void run() {
        while(true) {
            try {
                String responseJson = client.dis.readUTF();             // Listen
                Response response = GsonUtil.gson.fromJson(responseJson, Response.class);
                client.notifyListener(response.content.toLowerCase());  // Send to console
                if(response.connectionStatus.equals("unavailable")) {
                    client.closeConnection();
                    client.pauseListener();
                }
            } catch (IOException e) {
                client.notifyListener("Connection Terminated");
                client.closeConnection();
                client.pauseListener();
            }
        }
    }
}
