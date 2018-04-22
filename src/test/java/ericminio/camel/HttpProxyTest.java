package ericminio.camel;

import com.sun.net.httpserver.HttpServer;
import ericminio.support.HttpResponse;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;

import static ericminio.support.GetRequest.get;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpProxyTest {

    private HttpServer server;
    private CamelContext context;

    @Before
    public void startServer() throws Exception {
        server = HttpServer.create( new InetSocketAddress( 8000 ), 0 );
        server.createContext( "/", exchange -> {
            String body = "Hello World!";
            exchange.sendResponseHeaders( 200, body.length() );
            exchange.getResponseBody().write( body.getBytes() );
            exchange.close();
        } );
        server.start();
    }

    @Before
    public void startCamel() throws Exception {
        context = new DefaultCamelContext();
        context.start();
    }

    @After
    public void stopServer() {
        server.stop( 0 );
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
                from("jetty:http://localhost:8888/greeting")
                        .to("http4://localhost:8000?bridgeEndpoint=true");
            }
        });

        HttpResponse response = get( "http://localhost:8888/greeting" );

        assertThat(response.getStatusCode(), equalTo(200 ));
        assertThat(response.getBody(), equalTo( "Hello World!" ));
    }
}
