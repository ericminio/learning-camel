package ericminio.camel;

import ericminio.support.HttpResponse;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static ericminio.support.GetRequest.get;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HttpGetTest {

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
            from("jetty:http://localhost:8888/greeting")
                .setBody(constant("Hello World"));
            }
        });
        HttpResponse response = get( "http://localhost:8888/greeting" );

        assertThat(response.getStatusCode(), equalTo(200 ));
        assertThat(response.getBody(), equalTo( "Hello World" ));
    }

    @Test
    public void camelContextCanInterceptAnHttpCall() throws Exception {
        class Call {
            boolean wasMade;
        };
        Call call = new Call();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("jetty:http://localhost:8888/greeting")
                .process(exchange -> { call.wasMade = true; } )
                .setBody(constant("Hello World"));
            }
        });
        get( "http://localhost:8888/greeting" );

        assertThat(call.wasMade, equalTo(true));
    }

    @Test
    public void camelCanForceNotFound() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
            from("jetty:http://localhost:8888/greeting")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
                .setBody(simple("NOT FOUND"))
            ;
            }
        });
        HttpResponse response = get( "http://localhost:8888/greeting" );

        assertThat(response.getStatusCode(), equalTo(404 ));
        assertThat(response.getBody(), equalTo("NOT FOUND" ));
    }
}
