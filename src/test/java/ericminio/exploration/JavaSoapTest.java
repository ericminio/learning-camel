package ericminio.exploration;

import ericminio.support.HelloWorld;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Endpoint;
import java.net.URL;

import static ericminio.support.SOAPMessageToString.stringify;
import static ericminio.support.SOAPMessages.helloRequest;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class JavaSoapTest {

    private Endpoint service;
    private SOAPConnection connection;

    @Before
    public void startService() throws Exception {
        service = Endpoint.publish("http://localhost:9901/greeting", new HelloWorld());
        connection = SOAPConnectionFactory.newInstance().createConnection();
    }
    @After
    public void stopService() throws Exception {
        connection.close();
        service.stop();
    }

    @Test
    public void canCallSoapService() throws Exception {
        SOAPMessage reply = connection.call(helloRequest(), new URL( "http://localhost:9901/greeting" ));
        String response = stringify(reply);

        assertThat(response, containsString("<return>Hello world!</return>"));
    }
}
