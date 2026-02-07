package tlmi.communcator.atlmiclient.view;

import java.util.function.Consumer;

/**
 * Interface between MainController and the UI layer.
 * Implementations: SwingMainView (GUI), CliMainView (CLI/headless).
 */
public interface IMainView {

    // --- Status indicators ---
    void setInternetStatus(boolean live);
    void setLoginStatus(boolean live);
    void setWebsocketStatus(boolean live);

    // --- Partner list ---
    void addPartner(String name, String humanName, String avatar);

    /**
     * Register a listener that fires when a partner is selected.
     * Consumer receives String[] { name, humanName, avatar }.
     */
    void setPartnerSelectionListener(Consumer<String[]> listener);

    // --- Chat ---

    /**
     * Register a callback that fires when the user wants to send a message.
     * Consumer receives the message text.
     */
    void setSendAction(Consumer<String> sendCallback);

    void displayMessage(String message, boolean incoming);

    // --- User info ---
    void setSelfName(String name);
    void setSelfAvatar(String base64Avatar);
    void setPartnerAvatar(String base64Avatar);
    void setConnected();

    // --- Navigation ---
    void showPanel(String panelId);

    // --- Logging ---
    void addLog(String message);

}
