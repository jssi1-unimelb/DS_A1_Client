import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class DictionaryClientGUI extends JFrame implements KeyListener, ActionListener, EventListener {

    private final JTextField inputField;
    private final JTextArea outputArea;
    private final JPanel panel;
    private final JButton submitButton;
    private final Client client;

    public DictionaryClientGUI(Client client) {
        this.client = client;
        this.inputField = new JTextField();
        this.outputArea = new JTextArea(10, 40);
        this.panel = new JPanel(new GridBagLayout());
        int fontSize = 16;

        JFrame frame = new JFrame("Dictionary");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null);

        panel.setBackground(new Color(64, 64, 64));
        GridBagConstraints gbc = new GridBagConstraints();

        // Input Field
        inputField.setBackground(new Color(23, 23, 23));
        inputField.setBorder(new LineBorder(new Color(23, 23, 23), 2));
        inputField.setForeground(Color.WHITE);
        inputField.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        inputField.addKeyListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 5);
        panel.add(inputField, gbc);

        // Submit Button
        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(10, 5, 10, 10);
        submitButton = new JButton("Submit");
        submitButton.setBackground(Color.WHITE);
        submitButton.addActionListener(this);
        panel.add(submitButton, gbc);

        // Output Field
        outputArea.setBackground(new Color(23, 23, 23));
        outputArea.setBorder(new LineBorder(new Color(23, 23, 23), 2));
        outputArea.setForeground(Color.WHITE);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setFocusable(false);     // Prevent focus/caret
        outputArea.setHighlighter(null);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, fontSize));

        // Start up text
        String startUpMsg = """
                Welcome to this simple dictionary app, here is a list of available commands
                "start()":
                attempts to connect the app to the server
                "exit()":
                closes the connection to the server
                "meaning('word')":
                Fetches the meaning of the specified word
                "new(word, meaning1, meaning2, ...):
                Creates a new word with the specified meaning(s)
                "remove(word)":
                Removes the specified word
                "add_meaning(word, meaning)":
                Adds a new meaning to the specified word
                "update(word, old_meaning, new_meaning)":
                Updates the old meaning of the specified word with the new meaning
                
                """;
        outputArea.setText(startUpMsg);

        // Wrap it in a scroll pane
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2; // Span both columns
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(scrollPane, gbc);

        frame.add(panel);
        frame.setVisible(true);

        ImageIcon image = new ImageIcon("src/icon.png"); // Creates image icon
        frame.setIconImage(image.getImage()); // Sets icon of frame
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submitButton) {
            sendRequest(inputField.getText().toLowerCase());
        }
    }

    public void sendRequest(String request) {
        inputField.setText(""); // Clear input field once command is sent
        client.sendRequest(request);
    }

    @Override
    public void onEvent(String msg) {
        outputArea.append(msg + "\n");
        // Set the scroll pane to the bottom to show the most recent response
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == 10) { // Enter key pressed
            sendRequest(inputField.getText().toLowerCase());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
