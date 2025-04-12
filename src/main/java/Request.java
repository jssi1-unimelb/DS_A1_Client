import java.util.ArrayList;
import java.util.Arrays;

public class Request {
    public String command;
    public String[] parameters;

    public Request(String command, String[] parameters) {
        this.command = command;
        this.parameters = parameters;
    }
}
