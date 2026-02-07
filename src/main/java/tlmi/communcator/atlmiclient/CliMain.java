package tlmi.communcator.atlmiclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tlmi.communcator.atlmiclient.view.CliMainView;

/**
 * CLI (headless) entry point for the Tolmi Desktop Client.
 * Runs the same initialization sequence as the Swing version,
 * but all output goes to Log4j2 instead of the GUI.
 *
 * Usage: mvn exec:java -Dexec.args="--cli"
 */
public class CliMain {

    private static final Logger LOG = LogManager.getLogger(CliMain.class);

    public static void main(String[] args) {
        LOG.info("=== Tolmi Desktop Client — CLI Mode ===");

        CliMainView view = new CliMainView();
        MainController controller = new MainController(view, true);

        LOG.info("starting initialization...");
        controller.initialize();

        LOG.info("initialization complete. partners loaded: {}", view.getPartners().size());
        LOG.info("log entries: {}", view.getLogEntries().size());

        // Print summary
        LOG.info("--- Partner list ---");
        for (String[] p : view.getPartners()) {
            LOG.info("  {} ({})", p[1], p[0]);
        }

        LOG.info("--- Log entries ---");
        for (String entry : view.getLogEntries()) {
            LOG.info("  {}", entry);
        }

        LOG.info("=== CLI Mode finished ===");
    }

}
