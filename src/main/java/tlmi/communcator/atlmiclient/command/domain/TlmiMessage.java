package tlmi.communcator.atlmiclient.command.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tlmi.communcator.atlmiclient.command.algebra.TlmiMessageAlgebra;

public class TlmiMessage extends TlmiMessageAlgebra {

    private static final Logger LOG = LogManager.getLogger(TlmiMessage.class);

    public TlmiMessage(){
        setCommandName("TLMIMESSAGE");
    }

    public void process(){
        LOG.info("message arrived: {}", getMessage());
    }

}