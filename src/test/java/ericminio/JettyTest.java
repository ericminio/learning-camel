package ericminio;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class JettyTest {

    private CamelContext context;

    @Before
    public void startCamel() throws Exception {
        context = new DefaultCamelContext();
        context.start();
    }

    @After
    public void stopCamel() throws Exception {
        context.stop();
    }

    @Test
    public void camelContextCanExposeAnHttpEndpoint() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8888/greeting").setBody(constant("Hello, world!"));
            }
        });

        HttpURLConnection request = (HttpURLConnection) new URL( "http://localhost:8888/greeting" ).openConnection();
        assertThat( request.getResponseCode(), equalTo( 200 ) );
        assertThat(getResponseBody(request), equalTo( "Hello, world!" ) );
    }

    private String getResponseBody(HttpURLConnection request) throws IOException {
        InputStream inputStream = request.getInputStream();
        byte[] response = new byte[ inputStream.available() ];
        inputStream.read( response );
        return new String(response);
    }

    @Test
    public void camelContextCanInterceptAnHttpCall() throws Exception {
        MyProcessor processor = new MyProcessor();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8888/greeting")
                        .process(processor)
                        .setBody(constant("Hello, world!"));
            }
        });

        HttpURLConnection request = (HttpURLConnection) new URL( "http://localhost:8888/greeting" ).openConnection();
        request.getResponseCode();

        assertThat(processor.called, equalTo(true));
    }
}
