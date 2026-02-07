package tlmi.communcator.atlmiclient.command.domain;


import tlmi.communcator.atlmiclient.command.algebra.TlmiCMDInvitationAlgebra;

public class TlmiCMDInvitation extends TlmiCMDInvitationAlgebra {

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
        System.out.println("invitation arrived:");
    }

}