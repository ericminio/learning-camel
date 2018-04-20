package ericminio;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ActiveMqTest {

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
    public void camelCanReadFromActiveMq() throws Exception {
        final MyProcessor processor = new MyProcessor();
        context.addComponent("activemq", ActiveMQComponent.activeMQComponent("vm://localhost?broker.persistent=false"));
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:test.queue").process(processor);
            }
        });
        ProducerTemplate template = context.createProducerTemplate();
        template.sendBody("activemq:test.queue", "Hello World");
        Thread.sleep(300);

        assertThat(processor.message, equalTo("Hello World"));
    }
}
