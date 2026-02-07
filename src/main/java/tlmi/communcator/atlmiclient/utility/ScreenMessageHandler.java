package tlmi.communcator.atlmiclient.utility;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ScreenMessageHandler {

    public void message(String text) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, text, "Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }

    public void errorNotifying(String text) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, text, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }

}
