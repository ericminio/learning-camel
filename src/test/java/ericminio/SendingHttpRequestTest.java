package ericminio;

import com.sun.net.httpserver.HttpServer;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class SendingHttpRequestTest {

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

        HttpURLConnection request = (HttpURLConnection) new URL( "http://localhost:8888/greeting" ).openConnection();
        assertThat( request.getResponseCode(), equalTo( 200 ) );
        assertThat(getResponseBody(request), equalTo( "Hello World!" ) );
    }

    private String getResponseBody(HttpURLConnection request) throws IOException {
        InputStream inputStream = request.getInputStream();
        byte[] response = new byte[ inputStream.available() ];
        inputStream.read( response );
        return new String(response);
    }

}
