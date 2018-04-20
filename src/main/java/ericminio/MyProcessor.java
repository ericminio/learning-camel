package ericminio;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class MyProcessor implements Processor {

    public String message;

    public void process(Exchange exchange) throws Exception {
        this.message = exchange.getMessage().getBody().toString();
    }
}
