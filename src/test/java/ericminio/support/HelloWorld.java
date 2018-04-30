package ericminio.support;

import javax.jws.WebService;

@WebService(endpointInterface = "ericminio.support.Greeting")
public class HelloWorld implements Greeting {

    @Override
    public String hello() {
        return "Hello world!";
    }
}
