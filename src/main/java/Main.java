// Jiachen Si 1085839
public class Main {
    public static void main(String[] args) {
        try {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            DictionaryClient client = new DictionaryClient(host, port);
            DictionaryClientGUI gui = new DictionaryClientGUI(client);
            client.addListener(gui);
        } catch (NumberFormatException e) {
            System.out.println("Runtime Exception: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid parameters provided");
        }
    }
}
