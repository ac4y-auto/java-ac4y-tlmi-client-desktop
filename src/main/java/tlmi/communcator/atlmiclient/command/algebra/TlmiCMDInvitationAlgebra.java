package tlmi.communcator.atlmiclient.command.algebra;

import ac4y.command.domain.Ac4yCommand;

public class TlmiCMDInvitationAlgebra extends Ac4yCommand {

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    private String partner;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private String language;

}