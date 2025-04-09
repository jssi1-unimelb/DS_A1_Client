public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        DictionaryClientGUI gui = new DictionaryClientGUI(client);
        client.addListener(gui);
//        client.startConnection();
    }
}
