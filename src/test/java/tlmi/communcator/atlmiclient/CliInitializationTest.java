package tlmi.communcator.atlmiclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import tlmi.communcator.atlmiclient.view.CliMainView;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLI initialization test with TRACE-level logging.
 *
 * Log output goes to ./logs/tlmi-client-test.log at TRACE level.
 */
class CliInitializationTest {

    private static final Logger LOG = LogManager.getLogger(CliInitializationTest.class);

    @Test
    void testCliInitializationSequence() {
        LOG.info("=== CliInitializationTest START (production config) ===");

        CliMainView view = new CliMainView();
        MainController controller = new MainController(view, true);

        LOG.info("calling controller.initialize()...");
        controller.initialize();
        LOG.info("controller.initialize() returned");

        assertFalse(view.getLogEntries().isEmpty(),
                "Expected at least one log entry from initialization");

        boolean hasLocaleLog = view.getLogEntries().stream()
                .anyMatch(e -> e.contains("locale.getLanguage:"));
        assertTrue(hasLocaleLog, "Locale language log entry should always be present");

        File testLogFile = new File("./logs/tlmi-client-test.log");
        assertTrue(testLogFile.exists(), "Test log file should exist");
        assertTrue(testLogFile.length() > 0, "Test log file should not be empty");

        LOG.info("=== CliInitializationTest COMPLETE ===");
    }

    @Test
    void testCliInitializationWithLocalServer() {
        LOG.info("=== CliInitializationTest START (local config) ===");

        CliMainView view = new CliMainView();
        MainController controller = new MainController(view, true, ServerConfig.local());

        LOG.info("calling controller.initialize() with ServerConfig.local()...");
        controller.initialize();
        LOG.info("controller.initialize() returned");

        assertFalse(view.getLogEntries().isEmpty(),
                "Expected at least one log entry from initialization");

        // Check if we got further than the production test
        boolean loginSucceeded = view.getLogEntries().stream()
                .anyMatch(e -> e.contains("login successed!"));
        boolean loginFailed = view.getLogEntries().stream()
                .anyMatch(e -> e.contains("you dont have permission to login"));
        boolean serverReachable = view.getLogEntries().stream()
                .anyMatch(e -> e.contains("server is reachable"));

        LOG.info("server reachable: {}", serverReachable);
        LOG.info("login succeeded: {}", loginSucceeded);
        LOG.info("login failed: {}", loginFailed);

        if (loginSucceeded) {
            LOG.info("LOCAL SERVER: login OK! Checking WebSocket and further steps...");
            boolean wsOpen = view.getLogEntries().stream()
                    .anyMatch(e -> e.contains("WS open"));
            LOG.info("WebSocket opened: {}", wsOpen);
            assertNotNull(view.getSendCallback(), "Send callback should be registered after successful init");
        }

        for (String entry : view.getLogEntries()) {
            LOG.info("  > {}", entry);
        }

        LOG.info("=== CliInitializationTest (local) COMPLETE ===");
    }

}
