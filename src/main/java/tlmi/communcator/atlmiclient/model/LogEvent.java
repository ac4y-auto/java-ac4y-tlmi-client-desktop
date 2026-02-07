package tlmi.communcator.atlmiclient.model;

public class LogEvent {

    public LogEvent(String message){
        setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

}