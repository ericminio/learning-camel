package ericminio.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConditionalTest {

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
    public void allowsSeveralPathsInOneRoute() throws Exception {
        class Response {
            public String body;
        }
        Response response = new Response();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .choice()
                        .when(body().contains("fr"))
                            .process(exchange -> {
                                response.body = "salut";
                            })
                            .endChoice()
                        .otherwise()
                            .process(exchange -> {
                                response.body = "hi";
                            })
                ;
            }
        });
        ProducerTemplate template = context.createProducerTemplate();

        template.sendBody("direct:start", "fr");
        assertThat(response.body, equalTo("salut"));

        template.sendBody("direct:start", "en");
        assertThat(response.body, equalTo("hi"));
    }
}
