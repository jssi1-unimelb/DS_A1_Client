// Jiachen Si 1085839
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
    private final DictionaryClient client;

    public DictionaryClientGUI(DictionaryClient client) {
        this.client = client;
        this.inputField = new JTextField();
        this.outputArea = new JTextArea(10, 40);
        this.panel = new JPanel(new GridBagLayout());
        int fontSize = 16;

        JFrame frame = new JFrame("Dictionary");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 575);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        panel.setBackground(new Color(64, 64, 64));
        GridBagConstraints gbc = new GridBagConstraints();

        // Commands
        String commandsHelp = """
                Available Commands
                
                start()
                attempts to connect the app to the server
                
                exit()
                closes the connection to the server
                
                meaning(word)
                Fetches the meaning of the specified word
                
                new(word, meaning1, meaning2, ...)
                Creates a new word with the specified meaning(s)
                
                remove(word)
                Removes the specified word
               
                add_meaning(word, meaning)
                Adds a new meaning to the specified word
                
                update(word, old_meaning, new_meaning)
                Updates the old meaning of the specified word with the new meaning""";

        JTextArea commandsArea = new JTextArea(21, 30);
        commandsArea.setBackground(Color.WHITE);
        commandsArea.setBorder(new LineBorder(new Color(23, 23, 23), 2));
        commandsArea.setForeground(Color.BLACK);
        commandsArea.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        commandsArea.setLineWrap(true);
        commandsArea.setWrapStyleWord(true);
        commandsArea.setText(commandsHelp);
        commandsArea.setEditable(false);
        commandsArea.setFocusable(false);
        commandsArea.setHighlighter(null);
        JPanel commandsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints commandsGbc = new GridBagConstraints();
        commandsGbc.fill = GridBagConstraints.BOTH;
        commandsGbc.insets = new Insets(5,5,5,5);
        commandsPanel.add(commandsArea, commandsGbc);

        // Add the commands Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(commandsPanel, gbc);

        // Console Area
        JPanel consolePanel = new JPanel(new GridBagLayout());
        GridBagConstraints consoleGbc = new GridBagConstraints();

        // Input Field
        inputField.setBackground(new Color(23, 23, 23));
        inputField.setBorder(new LineBorder(new Color(23, 23, 23), 2));
        inputField.setForeground(Color.WHITE);
        inputField.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        inputField.addKeyListener(this);
        consoleGbc.gridx = 0;
        consoleGbc.gridy = 0;
        consoleGbc.weighty = 0;
        consoleGbc.weightx = 1;
        consoleGbc.insets = new Insets(10, 10, 10, 5);
        consoleGbc.fill = GridBagConstraints.HORIZONTAL;
        consolePanel.add(inputField, consoleGbc);

        // Submit button
        submitButton = new JButton("Submit");
        submitButton.setBackground(Color.WHITE);
        submitButton.addActionListener(this);
        consoleGbc.gridx = 1;
        consoleGbc.gridy = 0;
        consoleGbc.weightx = 0;
        consoleGbc.insets = new Insets(10, 5, 10, 10);
        consolePanel.add(submitButton, consoleGbc);

        // Scroll Pane + output area
        outputArea.setBackground(new Color(23, 23, 23));
        outputArea.setBorder(new LineBorder(new Color(23, 23, 23), 2));
        outputArea.setForeground(Color.WHITE);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setFocusable(false);
        outputArea.setHighlighter(null);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consoleGbc.gridx = 0;
        consoleGbc.gridy = 1;
        consoleGbc.gridwidth = 2; // Span both columns
        consoleGbc.weighty = 1;
        consoleGbc.weightx = 1;
        consoleGbc.fill = GridBagConstraints.BOTH;
        consoleGbc.insets = new Insets(10, 10, 10, 10);
        consolePanel.add(scrollPane, consoleGbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.9;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 5, 10, 10);
        panel.add(consolePanel, gbc);

        frame.add(panel);
        frame.setVisible(true);

        ImageIcon image = new ImageIcon("src/icon.png"); // Creates image icon
        frame.setIconImage(image.getImage()); // Sets icon of frame
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submitButton) {
            sendRequest();
        }
    }

    private void sendRequest() {
        String req = inputField.getText().toLowerCase().trim();
        if(!req.isEmpty()) { // Don't process request if it is empty
            client.sendRequest(req);
        }
        inputField.setText(""); // Clear input field
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
            sendRequest();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
