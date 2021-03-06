package ericminio.camel;

import ericminio.support.HelloWorld;
import ericminio.support.SOAPResponse;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.Endpoint;

import static ericminio.support.SOAPMessageToString.stringify;
import static ericminio.support.SOAPMessages.helloRequest;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class SoapTest {

    private Endpoint service;
    private CamelContext context;

    @Before
    public void startService() throws Exception {
        service = Endpoint.publish("http://localhost:9901/greeting", new HelloWorld());
    }
    @Before
    public void startCamel() throws Exception {
        context = new DefaultCamelContext();
        context.start();
    }
    @After
    public void stopService() {
        service.stop();
    }
    @After
    public void stopCamel() throws Exception {
        context.stop();
    }

    @Test
    public void canBeCalledByCamel() throws Exception {
        SOAPResponse response = new SOAPResponse();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("direct:start")
                    .setBody(constant(stringify(helloRequest())))
                    .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                    .setHeader(Exchange.CONTENT_TYPE, constant("text/xml;charset=UTF-8"))
                    .to("http4://localhost:9901/greeting")
                    .process(exchange -> response.body = exchange.getMessage().getBody(String.class));
            }
        });
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("direct:start", null);

        assertThat(response.body, containsString("<return>Hello world!</return>"));
    }
}
