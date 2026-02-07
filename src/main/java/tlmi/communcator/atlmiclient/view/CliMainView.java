package tlmi.communcator.atlmiclient.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * CLI (headless) IMainView implementation.
 * No Swing dependency — all output goes through Log4j2.
 * Stores partners in memory; logs every UI event at TRACE level.
 */
public class CliMainView implements IMainView {

    private static final Logger LOG = LogManager.getLogger(CliMainView.class);

    private final List<String[]> partners = new ArrayList<>();
    private final List<String> logEntries = new ArrayList<>();
    private Consumer<String[]> partnerSelectionListener;
    private Consumer<String> sendCallback;

    // --- Status indicators ---

    @Override
    public void setInternetStatus(boolean live) {
        LOG.trace("[STATUS] internet: {}", live ? "LIVE" : "ERROR");
    }

    @Override
    public void setLoginStatus(boolean live) {
        LOG.trace("[STATUS] login: {}", live ? "LIVE" : "ERROR");
    }

    @Override
    public void setWebsocketStatus(boolean live) {
        LOG.trace("[STATUS] websocket: {}", live ? "LIVE" : "ERROR");
    }

    // --- Partner list ---

    @Override
    public void addPartner(String name, String humanName, String avatar) {
        partners.add(new String[]{name, humanName, avatar});
        LOG.trace("[PARTNER] added: {} ({})", humanName, name);
    }

    @Override
    public void setPartnerSelectionListener(Consumer<String[]> listener) {
        this.partnerSelectionListener = listener;
        LOG.trace("[VIEW] partner selection listener registered");
    }

    // --- Chat ---

    @Override
    public void setSendAction(Consumer<String> sendCallback) {
        this.sendCallback = sendCallback;
        LOG.trace("[VIEW] send action registered");
    }

    @Override
    public void displayMessage(String message, boolean incoming) {
        String direction = incoming ? "INCOMING" : "OUTGOING";
        LOG.trace("[CHAT] {}: {}", direction, message);
    }

    // --- User info ---

    @Override
    public void setSelfName(String name) {
        LOG.trace("[USER] self name: {}", name);
    }

    @Override
    public void setSelfAvatar(String base64Avatar) {
        LOG.trace("[USER] self avatar set (length={})",
                base64Avatar != null ? base64Avatar.length() : 0);
    }

    @Override
    public void setPartnerAvatar(String base64Avatar) {
        LOG.trace("[USER] partner avatar set (length={})",
                base64Avatar != null ? base64Avatar.length() : 0);
    }

    @Override
    public void setConnected() {
        LOG.trace("[STATUS] connection: CONNECTED");
    }

    // --- Navigation ---

    @Override
    public void showPanel(String panelId) {
        LOG.trace("[VIEW] show panel: {}", panelId);
    }

    // --- Logging ---

    @Override
    public void addLog(String message) {
        logEntries.add(message);
        LOG.trace("[LOG] {}", message);
    }

    // --- CLI-specific accessors (for testing) ---

    public List<String[]> getPartners() {
        return partners;
    }

    public List<String> getLogEntries() {
        return logEntries;
    }

    public Consumer<String[]> getPartnerSelectionListener() {
        return partnerSelectionListener;
    }

    public Consumer<String> getSendCallback() {
        return sendCallback;
    }

    /**
     * Simulate partner selection in CLI mode.
     */
    public void selectPartner(int index) {
        if (partnerSelectionListener != null && index >= 0 && index < partners.size()) {
            partnerSelectionListener.accept(partners.get(index));
        }
    }

    /**
     * Simulate sending a message in CLI mode.
     */
    public void sendMessage(String text) {
        if (sendCallback != null && text != null && !text.trim().isEmpty()) {
            sendCallback.accept(text.trim());
        }
    }

}
