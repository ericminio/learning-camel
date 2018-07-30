package ericminio.camel;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ActiveMqTest {

    private ActiveMQConnectionFactory connectionFactory;
    private Connection connection;

    @Before
    public void executeInOrder() throws Exception {
        startQueue();
        startListener();
        sendMessage();
    }
    @After
    public void stop() throws Exception {
        connection.close();
    }

    public void startQueue() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.start();
    }
    public void startListener() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connection = connectionFactory.createConnection();
        connection.start();
    }
    public void sendMessage() throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("any-subject");
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage("Hello");
        producer.send(message);
    }

    @Test
    public void canBroadcastMessage() throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue("any-subject");
        MessageConsumer consumer = session.createConsumer(destination);
        TextMessage message = (TextMessage) consumer.receive();

        assertThat(message.getText(), equalTo("Hello"));
    }
}
