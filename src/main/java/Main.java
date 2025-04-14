// Jiachen Si 1085839
public class Main {
    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        DictionaryClient client = new DictionaryClient(host, port);
        DictionaryClientGUI gui = new DictionaryClientGUI(client);
        client.addListener(gui);
    }
}
