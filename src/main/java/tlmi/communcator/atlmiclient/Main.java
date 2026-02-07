package tlmi.communcator.atlmiclient;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import tlmi.communcator.atlmiclient.ui.MainFrame;
import tlmi.communcator.atlmiclient.view.SwingMainView;

public class Main {

    public static void main(String[] args) {

        // CLI mode: --cli flag
        if (args.length > 0 && args[0].equals("--cli")) {
            CliMain.main(args);
            return;
        }

        // GUI mode (default)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            SwingMainView view = new SwingMainView(frame);

            // Run initialization on a background thread to not block the UI
            new Thread(() -> {
                MainController controller = new MainController(view);
                controller.initialize();
            }).start();
        });

    }

}
