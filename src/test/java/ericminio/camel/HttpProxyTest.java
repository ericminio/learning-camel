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

public class HttpProxyTest {

    private HttpServer thirdParty;
    private CamelContext context;

    @Before
    public void startThirdPartyServer() throws Exception {
        thirdParty = ThirdPartyServer.willAnswer("Hello World").listen(8111);
    }

    @Before
    public void startCamel() throws Exception {
        context = new DefaultCamelContext();
        context.start();
    }

    @After
    public void stopThirdPartyServer() {
        thirdParty.stop( 0 );
    }

    @After
    public void stopCamel() throws Exception {
        context.stop();
    }

    @Test
    public void camelContextMakeHttpGetRequest() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8000/greeting")
                        .to("http4://localhost:8111?bridgeEndpoint=true");
            }
        });

        HttpResponse response = get( "http://localhost:8000/greeting" );

        assertThat(response.getStatusCode(), equalTo(200 ));
        assertThat(response.getBody(), equalTo( "Hello World" ));
    }
}
