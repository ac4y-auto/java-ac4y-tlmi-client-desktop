package tlmi.communcator.atlmiclient.command.domain;

import tlmi.communcator.atlmiclient.command.algebra.TlmiMessageAlgebra;

public class TlmiMessage extends TlmiMessageAlgebra {

    public TlmiMessage(){
        setCommandName("TLMIMESSAGE");
    }

    public void process(){
        System.out.println("message arrived:"+ getMessage());
    }

}