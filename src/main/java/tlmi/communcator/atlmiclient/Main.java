package tlmi.communcator.atlmiclient;

import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import tlmi.communcator.atlmiclient.ui.MainFrame;
import tlmi.communcator.atlmiclient.view.SwingMainView;

/**
 * Main entry point for the Tolmi Desktop Client.
 *
 * Usage:
 *   mvn exec:java                                    (GUI, production servers)
 *   mvn exec:java -Dexec.args="--local"              (GUI, localhost)
 *   mvn exec:java -Dexec.args="--cli"                (CLI, production servers)
 *   mvn exec:java -Dexec.args="--cli --local"        (CLI, localhost)
 */
public class Main {

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);

        // CLI mode
        if (argList.contains("--cli")) {
            CliMain.main(args);
            return;
        }

        // GUI mode
        boolean local = argList.contains("--local");
        ServerConfig config = local ? ServerConfig.local() : ServerConfig.production();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);

            SwingMainView view = new SwingMainView(frame);

            new Thread(() -> {
                MainController controller = new MainController(view, false, config);
                controller.initialize();
            }).start();
        });

    }

}
