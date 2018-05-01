package ericminio.exploration;

import ericminio.support.HelloWorld;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.Endpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static ericminio.support.SOAPMessageToString.stringify;
import static ericminio.support.SOAPMessages.helloRequest;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpClientSoapTest {

    private Endpoint service;
    private CloseableHttpClient httpclient;

    @Before
    public void startService() {
        service = Endpoint.publish("http://localhost:9901/greeting", new HelloWorld());
        httpclient = HttpClients.createDefault();
    }
    @After
    public void stopService() throws Exception {
        httpclient.close();
        service.stop();
    }

    @Test
    public void canCallSoapService() throws Exception {
        HttpPost httpPost = new HttpPost("http://localhost:9901/greeting");
        httpPost.setHeader("content-type", "text/xml");
        httpPost.setEntity(new StringEntity(stringify(helloRequest())));
        CloseableHttpResponse response = httpclient.execute(httpPost);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(getBody(response), containsString("<return>Hello world!</return>"));
    }

    private String getBody(CloseableHttpResponse response) throws IOException {
        HttpEntity body = response.getEntity();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        body.writeTo(out);

        return new String(out.toByteArray());
    }
}
