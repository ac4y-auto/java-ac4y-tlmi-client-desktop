package tlmi.communcator.atlmiclient.command.domain;


import tlmi.communcator.atlmiclient.command.algebra.TlmiCMDInvitationAcceptAlgebra;

public class TlmiCMDInvitationAccept extends TlmiCMDInvitationAcceptAlgebra {

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
        System.out.println("invitation accept arrived:");
    }

}