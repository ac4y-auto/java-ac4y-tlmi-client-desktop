package tlmi.communcator.atlmiclient.command.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tlmi.communcator.atlmiclient.command.algebra.TlmiCMDInvitationAlgebra;

public class TlmiCMDInvitation extends TlmiCMDInvitationAlgebra {

    private static final Logger LOG = LogManager.getLogger(TlmiCMDInvitation.class);

    public TlmiCMDInvitation(){

        setCommandName("TLMICMDINVITATION");

    }

    public TlmiCMDInvitation(String partner, String language){

        this();

        if (partner!=null)
            setPartner(partner);

        if (language!=null)
            setLanguage(language);

    }

    public void process(){
        LOG.info("invitation arrived");
    }

}