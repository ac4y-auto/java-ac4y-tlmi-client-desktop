package tlmi.communcator.atlmiclient.ui;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {

    private final JLabel internetLabel;
    private final JLabel loginLabel;
    private final JLabel websocketLabel;
    private final JLabel recognitionLabel;
    private final JLabel synthesizerLabel;

    private static final Color COLOR_LIVE = new Color(0x4C, 0xAF, 0x50);    // green
    private static final Color COLOR_ERROR = new Color(0xF4, 0x43, 0x36);    // red
    private static final Color COLOR_INACTIVE = Color.GRAY;

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

        internetLabel = createIndicator("NET");
        loginLabel = createIndicator("LOGIN");
        websocketLabel = createIndicator("WS");
        recognitionLabel = createIndicator("STT");
        synthesizerLabel = createIndicator("TTS");

        add(internetLabel);
        add(loginLabel);
        add(websocketLabel);
        add(recognitionLabel);
        add(synthesizerLabel);
    }

    private JLabel createIndicator(String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(COLOR_INACTIVE);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return label;
    }

    private void setLive(JLabel label) {
        SwingUtilities.invokeLater(() -> label.setBackground(COLOR_LIVE));
    }

    private void setError(JLabel label) {
        SwingUtilities.invokeLater(() -> label.setBackground(COLOR_ERROR));
    }

    private void setInactive(JLabel label) {
        SwingUtilities.invokeLater(() -> label.setBackground(COLOR_INACTIVE));
    }

    // Public API
    public void internetLive()    { setLive(internetLabel); }
    public void internetError()   { setError(internetLabel); }
    public void loginLive()       { setLive(loginLabel); }
    public void loginError()      { setError(loginLabel); }
    public void websocketLive()   { setLive(websocketLabel); }
    public void websocketError()  { setError(websocketLabel); }
    public void recognitionLive() { setLive(recognitionLabel); }
    public void recognitionError(){ setError(recognitionLabel); }
    public void synthesizerLive() { setLive(synthesizerLabel); }
    public void synthesizerError(){ setError(synthesizerLabel); }

}
