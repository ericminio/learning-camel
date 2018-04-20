package ericminio;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ActiveMqTest {

    @Test
    public void camelIsAvailable() {
        CamelContext context = new DefaultCamelContext();

        assertThat(context, not(equalTo(null)));
    }

    @Test
    public void activeMqIsAvailable() {
        ActiveMQComponent queue = new ActiveMQComponent();

        assertThat(queue, not(equalTo(null)));
    }

    @Test
    public void camelCanReadFromActiveMq() throws Exception {
        CamelContext context = new DefaultCamelContext();
        try {
            final MyProcessor processor = new MyProcessor();
            context.addComponent("activemq", ActiveMQComponent.activeMQComponent("vm://localhost?broker.persistent=false"));
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("activemq:queue:test.queue").process(processor);
                }
            });
            ProducerTemplate template = context.createProducerTemplate();
            context.start();
            template.sendBody("activemq:test.queue", "Hello World");
            Thread.sleep(500);

            assertThat(processor.message, equalTo("Hello World"));
        }
        finally {
            context.stop();
        }
    }
}
