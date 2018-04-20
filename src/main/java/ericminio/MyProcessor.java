package ericminio;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MyProcessor implements Processor {

    public String message;
    public Boolean called;

    public MyProcessor() {
        this.called = false;
    }

    public void process(Exchange exchange) throws Exception {
        this.called = true;
        this.message = exchange.getMessage().getBody().toString();
    }
}
