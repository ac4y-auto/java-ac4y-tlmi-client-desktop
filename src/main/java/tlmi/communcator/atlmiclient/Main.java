package tlmi.communcator.atlmiclient;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import tlmi.communcator.atlmiclient.ui.MainFrame;

public class Main {

    public static void main(String[] args) {

        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            // Run initialization on a background thread to not block the UI
            new Thread(() -> {
                MainController controller = new MainController(frame);
                controller.initialize();
            }).start();
        });

    }

}
