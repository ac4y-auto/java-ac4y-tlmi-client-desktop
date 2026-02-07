package tlmi.communcator.atlmiclient.command.algebra;

//import ac4y.command.domain.Ac4yCommand;

import ac4y.command.domain.Ac4yCommand;

public class TlmiMessageAlgebra extends Ac4yCommand {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

}