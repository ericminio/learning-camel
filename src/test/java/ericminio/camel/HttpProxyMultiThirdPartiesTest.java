package ericminio.camel;

import com.sun.net.httpserver.HttpServer;
import ericminio.support.HttpResponse;
import ericminio.support.ThirdPartyServer;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static ericminio.support.GetRequest.get;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpProxyMultiThirdPartiesTest {

    private HttpServer thirdParty1;
    private HttpServer thirdParty2;
    private CamelContext context;

    @Before
    public void startThirdPartyServers() throws Exception {
        thirdParty1 = ThirdPartyServer.willAnswer("Hello").listen(8111);
        thirdParty2 = ThirdPartyServer.willAnswer("World").listen(8222);
    }

    @Before
    public void startCamel() throws Exception {
        context = new DefaultCamelContext();
        context.start();
    }

    @After
    public void stopThirdPartyServer() {
        thirdParty1.stop( 0 );
        thirdParty2.stop( 0 );
    }

    @After
    public void stopCamel() throws Exception {
        context.stop();
    }

    @Test
    public void camelContextMakeHttpGetRequest() throws Exception {
        class Message {
            public String start;
            public String end;
        }
        Message message = new Message();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8000/greeting")

                        .to("http4://localhost:8111?bridgeEndpoint=true")
                        .process(exchange -> {
                            message.start = exchange.getIn().getBody(String.class);
                        })
                        .to("http4://localhost:8222?bridgeEndpoint=true")
                        .process(exchange -> {
                            message.end = exchange.getIn().getBody(String.class);
                        })
                        .process(exchange -> {
                            exchange.getOut().setBody(message.start + " " + message.end);
                        })
                        ;
            }
        });

        HttpResponse response = get( "http://localhost:8000/greeting" );

        assertThat(response.getStatusCode(), equalTo(200 ));
        assertThat(response.getBody(), equalTo( "Hello World" ));
    }
}
