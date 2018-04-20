package ericminio;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TddReadyTest {

    @Test
    public void assertions() {
        assertThat(1+1, equalTo(2));
    }

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


}
