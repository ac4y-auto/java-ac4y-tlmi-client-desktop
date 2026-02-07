package tlmi.communcator.atlmiclient.ui;

import tlmi.communcator.atlmiclient.model.ChatEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ChatPanel extends JPanel {

    private final JPanel messagesPanel;
    private final JScrollPane scrollPane;
    private final JTextField inputField;
    private final JButton sendButton;
    private final List<ChatEvent> chatHistory = new ArrayList<>();

    public ChatPanel() {
        setLayout(new BorderLayout());

        // Messages area
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        inputField = new JTextField();
        inputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
    }

    public void addMessage(ChatEvent event) {
        chatHistory.add(event);
        SwingUtilities.invokeLater(() -> {
            JPanel bubble = createBubble(event);
            messagesPanel.add(bubble);
            messagesPanel.add(Box.createVerticalStrut(5));
            messagesPanel.revalidate();
            messagesPanel.repaint();
            // Scroll to bottom
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private JPanel createBubble(ChatEvent event) {
        JPanel wrapper = new JPanel(new FlowLayout(
                event.isIncoming() ? FlowLayout.LEFT : FlowLayout.RIGHT, 10, 2));
        wrapper.setBackground(Color.WHITE);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel label = new JLabel("<html><body style='width:250px;padding:5px'>"
                + escapeHtml(event.getMessage()) + "</body></html>");
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        if (event.isIncoming()) {
            label.setBackground(new Color(0xE0, 0xE0, 0xE0));
        } else {
            label.setBackground(new Color(0xBB, 0xDE, 0xFB));
        }

        wrapper.add(label);
        return wrapper;
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public void setSendAction(ActionListener action) {
        sendButton.addActionListener(action);
        inputField.addActionListener(action);
    }

    public String getInputText() {
        return inputField.getText();
    }

    public void clearInput() {
        inputField.setText("");
    }

}
