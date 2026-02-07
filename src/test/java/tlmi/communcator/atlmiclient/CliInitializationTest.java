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
 * This test runs the full initialization sequence (internet check,
 * user create, gate register, login, websocket, partner load)
 * through the CliMainView — no Swing GUI involved.
 *
 * Log output goes to ./logs/tlmi-client-test.log at TRACE level
 * (configured in src/test/resources/log4j2-test.xml).
 *
 * NOTE: The backend servers (client.ac4y.com, gate.ac4y.com) may be
 * offline, so the test verifies the initialization flow runs without
 * exceptions, not that all steps succeed.
 */
class CliInitializationTest {

    private static final Logger LOG = LogManager.getLogger(CliInitializationTest.class);

    @Test
    void testCliInitializationSequence() {
        LOG.info("=== CliInitializationTest START ===");

        // Arrange
        CliMainView view = new CliMainView();
        MainController controller = new MainController(view, true);

        // Act — run full init (may stop early if servers offline)
        LOG.info("calling controller.initialize()...");
        controller.initialize();
        LOG.info("controller.initialize() returned");

        // Assert: log entries were collected (always, even if servers offline)
        assertFalse(view.getLogEntries().isEmpty(),
                "Expected at least one log entry from initialization");

        LOG.info("log entries collected: {}", view.getLogEntries().size());
        for (String entry : view.getLogEntries()) {
            LOG.info("  > {}", entry);
        }

        // Assert: basic locale info was always logged
        boolean hasLocaleLog = view.getLogEntries().stream()
                .anyMatch(e -> e.contains("locale.getLanguage:"));
        assertTrue(hasLocaleLog,
                "Locale language log entry should always be present");

        // Assert: TRACE log file was created
        File testLogFile = new File("./logs/tlmi-client-test.log");
        assertTrue(testLogFile.exists(),
                "Test log file should exist at ./logs/tlmi-client-test.log");
        assertTrue(testLogFile.length() > 0,
                "Test log file should not be empty");

        LOG.info("test log file size: {} bytes", testLogFile.length());

        // Assert: either full init completed (partners loaded) or stopped at login
        boolean loginFailed = view.getLogEntries().stream()
                .anyMatch(e -> e.contains("you dont have permission to login"));
        boolean initCompleted = view.getLogEntries().stream()
                .anyMatch(e -> e.equals("start!"));

        assertTrue(loginFailed || initCompleted,
                "Init should either complete (start!) or stop at login failure");

        if (initCompleted) {
            LOG.info("Full initialization completed (servers online)");
            assertNotNull(view.getPartnerSelectionListener(),
                    "Partner selection listener should be registered when init completes");
            assertNotNull(view.getSendCallback(),
                    "Send callback should be registered when init completes");
            LOG.info("Partners loaded: {}", view.getPartners().size());
        } else {
            LOG.info("Initialization stopped at login (servers offline — expected in CI/test)");
        }

        LOG.info("=== CliInitializationTest COMPLETE ===");
    }

}
