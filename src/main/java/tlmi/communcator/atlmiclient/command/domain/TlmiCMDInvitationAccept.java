package tlmi.communcator.atlmiclient.command.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tlmi.communcator.atlmiclient.command.algebra.TlmiCMDInvitationAcceptAlgebra;

public class TlmiCMDInvitationAccept extends TlmiCMDInvitationAcceptAlgebra {

    private static final Logger LOG = LogManager.getLogger(TlmiCMDInvitationAccept.class);

    public TlmiCMDInvitationAccept(){

        setCommandName("TLMICMDINVITATIONACCEPT");

    }

    public TlmiCMDInvitationAccept(String partner, String language){

        this();

        if (partner!=null)
            setPartner(partner);

        if (language!=null)
            setLanguage(language);

    }

    public void process(){
        LOG.info("invitation accept arrived");
    }

}