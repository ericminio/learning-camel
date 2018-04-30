package ericminio.support;

import javax.xml.soap.*;

public class Messages {

    public static SOAPMessage helloRequest() throws Exception {
        MessageFactory myMsgFct = MessageFactory.newInstance();
        SOAPMessage message = myMsgFct.createMessage();
        SOAPPart mySPart = message.getSOAPPart();
        SOAPEnvelope myEnvp = mySPart.getEnvelope();
        SOAPBody body = myEnvp.getBody();
        Name bodyName = myEnvp.createName("hello", "any", "http://support.ericminio/");
        body.addBodyElement(bodyName);
        message.saveChanges();

        return message;
    }
}
