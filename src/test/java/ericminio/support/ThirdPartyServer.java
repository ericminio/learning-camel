package ericminio.support;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class ThirdPartyServer {

    private HttpServer server;
    private String body;

    public ThirdPartyServer(String body) {
        this.body = body;
    }

    public static ThirdPartyServer willAnswer(String body) {
        return new ThirdPartyServer(body);
    }

    public HttpServer listen(int port) throws Exception {
        this.server = HttpServer.create( new InetSocketAddress( port ), 0 );
        this.server.createContext( "/", exchange -> {
            exchange.sendResponseHeaders( 200, body.length() );
            exchange.getResponseBody().write( body.getBytes() );
            exchange.close();
        } );
        this.server.start();
        return this.server;
    }
}
