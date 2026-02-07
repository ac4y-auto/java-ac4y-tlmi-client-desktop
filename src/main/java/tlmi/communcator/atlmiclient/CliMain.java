package tlmi.communcator.atlmiclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import tlmi.communcator.atlmiclient.view.CliMainView;

/**
 * CLI (headless) entry point for the Tolmi Desktop Client.
 *
 * Usage:
 *   mvn exec:java -Dexec.args="--cli"              (production servers)
 *   mvn exec:java -Dexec.args="--cli --local"       (localhost:3000 + ws://localhost:2222)
 */
public class CliMain {

    private static final Logger LOG = LogManager.getLogger(CliMain.class);

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);
        boolean local = argList.contains("--local");

        ServerConfig config = local ? ServerConfig.local() : ServerConfig.production();

        LOG.info("=== Tolmi Desktop Client — CLI Mode ===");
        LOG.info("server config: {}", config);

        CliMainView view = new CliMainView();
        MainController controller = new MainController(view, true, config);

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
