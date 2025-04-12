
import java.io.IOException;

// Separates the listening blocking activity to its own thread
public class ServerReader extends Thread {
    private Client client;

    public ServerReader(Client client) {
        this.client = client;
    }

    public void run() {
        while(true) {
            try {
                String response = client.dis.readUTF().toLowerCase(); // Listen
                client.notifyListener(response);               // Send to console
            } catch (IOException e) {
                client.notifyListener("Connection timed out, inactive for too long");
                client.closeConnection();
            }

        }
    }
}
