package ericminio.support;

import java.io.ByteArrayOutputStream;

public class SOAPMessageToString {

    public static String stringify(javax.xml.soap.SOAPMessage message) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);

        return new String(out.toByteArray());
    }
}
