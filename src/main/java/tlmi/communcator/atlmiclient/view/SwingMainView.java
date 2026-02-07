package tlmi.communcator.atlmiclient.view;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import tlmi.communcator.atlmiclient.model.ChatEvent;
import tlmi.communcator.atlmiclient.ui.ImageUtil;
import tlmi.communcator.atlmiclient.ui.MainFrame;
import tlmi.communcator.atlmiclient.ui.PartnerListPanel;

/**
 * Swing-based IMainView implementation.
 * Delegates all calls to the existing MainFrame and its sub-panels.
 */
public class SwingMainView implements IMainView {

    private final MainFrame frame;

    public SwingMainView(MainFrame frame) {
        this.frame = frame;
    }

    public MainFrame getFrame() {
        return frame;
    }

    // --- Status indicators ---

    @Override
    public void setInternetStatus(boolean live) {
        if (live) frame.getStatusBar().internetLive();
        else      frame.getStatusBar().internetError();
    }

    @Override
    public void setLoginStatus(boolean live) {
        if (live) frame.getStatusBar().loginLive();
        else      frame.getStatusBar().loginError();
    }

    @Override
    public void setWebsocketStatus(boolean live) {
        if (live) frame.getStatusBar().websocketLive();
        else      frame.getStatusBar().websocketError();
    }

    // --- Partner list ---

    @Override
    public void addPartner(String name, String humanName, String avatar) {
        frame.getPartnerListPanel().addPartner(
                new PartnerListPanel.PartnerInfo(name, humanName, avatar)
        );
    }

    @Override
    public void setPartnerSelectionListener(Consumer<String[]> listener) {
        frame.getPartnerListPanel().getList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                PartnerListPanel.PartnerInfo selected =
                        frame.getPartnerListPanel().getList().getSelectedValue();
                if (selected != null) {
                    listener.accept(new String[]{
                            selected.getName(),
                            selected.getHumanName(),
                            selected.getAvatar()
                    });
                }
            }
        });
    }

    // --- Chat ---

    @Override
    public void setSendAction(Consumer<String> sendCallback) {
        frame.getChatPanel().setSendAction(e -> {
            String text = frame.getChatPanel().getInputText().trim();
            if (!text.isEmpty()) {
                sendCallback.accept(text);
                frame.getChatPanel().clearInput();
            }
        });
    }

    @Override
    public void displayMessage(String message, boolean incoming) {
        frame.getChatPanel().addMessage(new ChatEvent(message, incoming));
    }

    // --- User info ---

    @Override
    public void setSelfName(String name) {
        frame.setSelfName(name);
    }

    @Override
    public void setSelfAvatar(String base64Avatar) {
        frame.setSelfAvatar(ImageUtil.fromBase64(base64Avatar));
    }

    @Override
    public void setPartnerAvatar(String base64Avatar) {
        frame.setPartnerAvatar(ImageUtil.fromBase64(base64Avatar));
    }

    @Override
    public void setConnected() {
        frame.setConnected();
    }

    // --- Navigation ---

    @Override
    public void showPanel(String panelId) {
        frame.showPanel(panelId);
    }

    // --- Logging ---

    @Override
    public void addLog(String message) {
        frame.getLogPanel().addLog(message);
    }

}
