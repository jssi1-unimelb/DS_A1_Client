// Jiachen Si 1085839
import java.io.IOException;

// Separates the listening blocking activity to its own thread
public class ServerReader extends Thread {
    private final DictionaryClient client;

    public ServerReader(DictionaryClient client) {
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
                System.out.println(e.getMessage());
                if(e.getMessage().equals("Connection reset")) {
                    client.notifyListener("error: connection has been reset, please reconnect");
                }
                client.closeConnection();
                client.pauseListener();
            }
        }
    }
}
