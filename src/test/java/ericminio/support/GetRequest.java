package ericminio.support;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest {

    public static HttpResponse get(String url) throws Exception {
        HttpURLConnection request = (HttpURLConnection) new URL( url ).openConnection();
        HttpResponse response = new HttpResponse();
        response.setStatusCode(request.getResponseCode());
        response.setBody(getResponseBody(request));

        return response;
    }

    protected static String getResponseBody(HttpURLConnection request) throws IOException {
        InputStream inputStream = request.getInputStream();
        byte[] response = new byte[ inputStream.available() ];
        inputStream.read( response );
        return new String(response);
    }
}
