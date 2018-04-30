package ericminio.support;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
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
        this.server.createContext( "/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                exchange.sendResponseHeaders(200, body.length());
                exchange.getResponseBody().write(body.getBytes());
                exchange.close();
            }
        });
        this.server.start();
        return this.server;
    }
}
