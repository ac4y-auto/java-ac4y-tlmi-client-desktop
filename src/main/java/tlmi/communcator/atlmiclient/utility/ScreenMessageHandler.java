package tlmi.communcator.atlmiclient.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ScreenMessageHandler {

    private static final Logger LOG = LogManager.getLogger(ScreenMessageHandler.class);

    private final boolean cliMode;

    public ScreenMessageHandler() {
        this(false);
    }

    public ScreenMessageHandler(boolean cliMode) {
        this.cliMode = cliMode;
    }

    public void message(String text) {
        if (cliMode) {
            LOG.info("[MESSAGE] {}", text);
        } else {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, text, "Info", JOptionPane.INFORMATION_MESSAGE)
            );
        }
    }

    public void errorNotifying(String text) {
        if (cliMode) {
            LOG.error("[ERROR] {}", text);
        } else {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, text, "Error", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

}
