package tlmi.communcator.atlmiclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame {

    private final StatusBar statusBar;
    private final PartnerListPanel partnerListPanel;
    private final ChatPanel chatPanel;
    private final LogPanel logPanel;
    private final CardLayout cardLayout;
    private final JPanel centerPanel;

    // User info
    private final JLabel selfNameLabel;
    private final JLabel selfAvatarLabel;
    private final JLabel partnerNameLabel;
    private final JLabel partnerAvatarLabel;
    private final JLabel connectionLabel;

    // Navigation buttons
    private final JButton partnersButton;
    private final JButton chatButton;
    private final JButton logButton;

    public static final String CARD_PARTNERS = "partners";
    public static final String CARD_CHAT = "chat";
    public static final String CARD_LOG = "log";

    public MainFrame() {
        super("Tolmi Desktop Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Top area: status bar + user info ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        statusBar = new StatusBar();
        topPanel.add(statusBar);

        // User info row
        JPanel userInfoPanel = new JPanel(new BorderLayout(5, 0));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Self info (left)
        JPanel selfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        selfAvatarLabel = new JLabel();
        selfAvatarLabel.setPreferredSize(new Dimension(40, 40));
        selfNameLabel = new JLabel("...");
        selfNameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        selfPanel.add(selfAvatarLabel);
        selfPanel.add(selfNameLabel);

        // Connection indicator (center)
        connectionLabel = new JLabel("disconnected");
        connectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        connectionLabel.setForeground(Color.RED);
        connectionLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));

        // Partner info (right)
        JPanel partnerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        partnerNameLabel = new JLabel("...");
        partnerNameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        partnerAvatarLabel = new JLabel();
        partnerAvatarLabel.setPreferredSize(new Dimension(40, 40));
        partnerPanel.add(partnerNameLabel);
        partnerPanel.add(partnerAvatarLabel);

        userInfoPanel.add(selfPanel, BorderLayout.WEST);
        userInfoPanel.add(connectionLabel, BorderLayout.CENTER);
        userInfoPanel.add(partnerPanel, BorderLayout.EAST);

        topPanel.add(userInfoPanel);
        add(topPanel, BorderLayout.NORTH);

        // --- Center: card layout for switchable panels ---
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);

        partnerListPanel = new PartnerListPanel();
        chatPanel = new ChatPanel();
        logPanel = new LogPanel();

        centerPanel.add(partnerListPanel, CARD_PARTNERS);
        centerPanel.add(chatPanel, CARD_CHAT);
        centerPanel.add(logPanel, CARD_LOG);

        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom: navigation buttons ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        partnersButton = new JButton("Partners");
        chatButton = new JButton("Chat");
        logButton = new JButton("Log");

        partnersButton.addActionListener(e -> showPanel(CARD_PARTNERS));
        chatButton.addActionListener(e -> showPanel(CARD_CHAT));
        logButton.addActionListener(e -> showPanel(CARD_LOG));

        navPanel.add(partnersButton);
        navPanel.add(chatButton);
        navPanel.add(logButton);
        add(navPanel, BorderLayout.SOUTH);
    }

    public void showPanel(String cardName) {
        cardLayout.show(centerPanel, cardName);
    }

    public StatusBar getStatusBar() { return statusBar; }
    public PartnerListPanel getPartnerListPanel() { return partnerListPanel; }
    public ChatPanel getChatPanel() { return chatPanel; }
    public LogPanel getLogPanel() { return logPanel; }

    public void setSelfName(String name) {
        SwingUtilities.invokeLater(() -> selfNameLabel.setText(name));
    }

    public void setPartnerName(String name) {
        SwingUtilities.invokeLater(() -> partnerNameLabel.setText(name));
    }

    public void setSelfAvatar(BufferedImage img) {
        SwingUtilities.invokeLater(() -> {
            if (img != null) {
                Image scaled = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                selfAvatarLabel.setIcon(new ImageIcon(scaled));
            }
        });
    }

    public void setPartnerAvatar(BufferedImage img) {
        SwingUtilities.invokeLater(() -> {
            if (img != null) {
                Image scaled = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                partnerAvatarLabel.setIcon(new ImageIcon(scaled));
            }
        });
    }

    public void setConnected() {
        SwingUtilities.invokeLater(() -> {
            connectionLabel.setText("connected");
            connectionLabel.setForeground(new Color(0x4C, 0xAF, 0x50));
        });
    }

    public void setDisconnected() {
        SwingUtilities.invokeLater(() -> {
            connectionLabel.setText("disconnected");
            connectionLabel.setForeground(Color.RED);
        });
    }

}
