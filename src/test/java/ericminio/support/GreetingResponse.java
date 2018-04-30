package ericminio.support;

import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;

public class GreetingResponse {

    public static String stringify(SOAPMessage message) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);

        return new String(out.toByteArray());
    }
}
