package ericminio.exploration;

import ericminio.support.Greeting;
import ericminio.support.HelloWorld;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JavaWsTest {

    private Endpoint service;

    @Before
    public void startService() {
        service = Endpoint.publish("http://localhost:9901/greeting", new HelloWorld());
    }
    @After
    public void stopService() {
        service.stop();
    }

    @Test
    public void canCallSoapService() throws Exception {
        URL url = new URL( "http://localhost:9901/greeting?wsdl" );
        Service helloService = Service.create(url, new QName("http://support.ericminio/", "HelloWorldService"));
        Greeting greeting = helloService.getPort(new QName("http://support.ericminio/", "HelloWorldPort"), Greeting.class);

        assertThat(greeting.hello(), equalTo("Hello world!"));
    }
}
