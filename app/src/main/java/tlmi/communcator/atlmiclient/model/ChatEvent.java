package tlmi.communcator.atlmiclient.model;

public class ChatEvent {

    public ChatEvent(String message, boolean incoming){
        setMessage(message);
        setIncoming(incoming);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    private boolean incoming = false;

}