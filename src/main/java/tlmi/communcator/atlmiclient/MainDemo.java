package tlmi.communcator.atlmiclient;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import tlmi.communcator.atlmiclient.model.ChatEvent;
import tlmi.communcator.atlmiclient.ui.MainFrame;
import tlmi.communcator.atlmiclient.ui.PartnerListPanel;

/**
 * UI demo with mock data - no ac4y/tlmi dependencies needed.
 * Tests the Swing UI independently.
 */
public class MainDemo {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            // Simulate status bar indicators
            frame.getStatusBar().internetLive();
            frame.getStatusBar().loginLive();
            frame.getStatusBar().websocketLive();

            // Simulate user info
            frame.setSelfName("TestUser (hu)");
            frame.setPartnerName("Partner (en)");

            // Simulate partner list
            frame.getPartnerListPanel().addPartner(
                    new PartnerListPanel.PartnerInfo("user-001", "Alice Johnson", null));
            frame.getPartnerListPanel().addPartner(
                    new PartnerListPanel.PartnerInfo("user-002", "Bob Smith", null));
            frame.getPartnerListPanel().addPartner(
                    new PartnerListPanel.PartnerInfo("user-003", "Carlos Garcia", null));

            // Partner click -> switch to chat
            frame.getPartnerListPanel().getList().addListSelectionListener(ev -> {
                if (!ev.getValueIsAdjusting()) {
                    PartnerListPanel.PartnerInfo selected =
                            frame.getPartnerListPanel().getList().getSelectedValue();
                    if (selected != null) {
                        frame.setPartnerName(selected.getHumanName());
                        frame.setConnected();
                        frame.showPanel(MainFrame.CARD_CHAT);
                        frame.getLogPanel().addLog("invitation sent to: " + selected.getName());
                    }
                }
            });

            // Simulate log messages
            frame.getLogPanel().addLog("start!");
            frame.getLogPanel().addLog("locale.getCountry:HU");
            frame.getLogPanel().addLog("locale.getLanguage:hu");
            frame.getLogPanel().addLog("login successed!");
            frame.getLogPanel().addLog("WS open");

            // Simulate chat - send action
            frame.getChatPanel().setSendAction(e -> {
                String text = frame.getChatPanel().getInputText().trim();
                if (!text.isEmpty()) {
                    frame.getChatPanel().addMessage(new ChatEvent(text, false));
                    frame.getChatPanel().clearInput();
                    frame.getLogPanel().addLog("send message:" + text);

                    // Simulate incoming reply after 1 second
                    Timer timer = new Timer(1000, ev -> {
                        frame.getChatPanel().addMessage(
                                new ChatEvent("Echo: " + text, true)
                        );
                        frame.getLogPanel().addLog("message arrived: Echo: " + text);
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            });

            // Add some initial chat messages
            frame.getChatPanel().addMessage(new ChatEvent("Hello! How are you?", true));
            frame.getChatPanel().addMessage(new ChatEvent("Szia! Jol vagyok!", false));
            frame.getChatPanel().addMessage(new ChatEvent("Great to hear!", true));

            // Simulate connection
            frame.setConnected();
        });

    }

}
