package ericminio.camel;

import net.sf.json.JSONObject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.json.simple.JsonObject;
import org.apache.camel.json.simple.Jsoner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class XmlToJsonTest {

    private CamelContext context;

    @Before
    public void startCamel() throws Exception {
        context = new DefaultCamelContext();
        context.start();
    }
    @After
    public void stopCamel() throws Exception {
        context.stop();
    }

    @Test
    public void canBeDoneByCamel() throws Exception {
        class Response {
            public String body;
        }
        Response response = new Response();
        XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
        xmlJsonFormat.setEncoding("UTF-8");
        xmlJsonFormat.setForceTopLevelObject(true);
        xmlJsonFormat.setTrimSpaces(true);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                .marshal(xmlJsonFormat)
                .process(exchange -> {
                    response.body = exchange.getIn().getBody(String.class);
                });
                ;
            }
        });
        ProducerTemplate template = context.createProducerTemplate();
        String xml = "<any><value> 42  </value></any>";
        template.sendBody("direct:start", xml);

        assertThat(response.body, equalTo("{\"any\":{\"value\":\"42\"}}"));
    }
}
