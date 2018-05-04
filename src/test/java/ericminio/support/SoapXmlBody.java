package ericminio.support;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SoapXmlBody {

    @Test
    public void bodyCanBeExtracted() {
        String message = "<?xml version=\"1.0\" ?><S:Envelope xmlns:S=\"http://any/envelope/\"><S:Body>any-body</S:Body></S:Envelope>";

        assertThat(SoapXmlBody.extractBodyFrom(message), equalTo("any-body"));
    }

    @Test
    public void bodyCanBeExtractedWhateverTheEnvelop() {
        String message = "<?xml version=\"1.0\" ?><ANY-ENV:Envelope xmlns:ANY-ENV=\"http://any/envelope/\"><ANY-ENV:Body>any-body</ANY-ENV:Body></ANY-ENV:Envelope>";

        assertThat(SoapXmlBody.extractBodyFrom(message), equalTo("any-body"));
    }

    @Test
    public void resistsEmpty() {
        String message = "<?xml version=\"1.0\" ?><ANY-ENV:Envelope xmlns:ANY-ENV=\"http://any/envelope/\"><ANY-ENV:Body></ANY-ENV:Body></ANY-ENV:Envelope>";

        assertThat(SoapXmlBody.extractBodyFrom(message), equalTo(""));
    }

    @Test
    public void resistsAbsence() {
        String message = "<?xml version=\"1.0\" ?><ANY-ENV:Envelope xmlns:ANY-ENV=\"http://any/envelope/\"></ANY-ENV:Envelope>";

        assertThat(SoapXmlBody.extractBodyFrom(message), equalTo(""));
    }

    @Test
    public void resistsVoid() {
        assertThat(SoapXmlBody.extractBodyFrom(""), equalTo(""));
    }

    @Test
    public void canExtractNamespace() {
        String message = "<?xml version=\"1.0\" ?><ANY-ENV:Envelope xmlns:ANY-ENV=\"http://any/envelope/\"><ANY-ENV:Body>any-body</ANY-ENV:Body></ANY-ENV:Envelope>";

        assertThat(getNamespace(message), equalTo("ANY-ENV"));
    }

    public static String extractBodyFrom(String message) {

        String xmlns = getNamespace(message);

        int startIndex = message.indexOf("<" + xmlns + ":Body>");
        if (startIndex == -1) { return ""; }

        String trailing = message.substring(startIndex);
        int endIndex = trailing.indexOf("</" + xmlns + ":Body>");

        return trailing.substring(("<" + xmlns + ":Body>").length(), endIndex);
    }

    private static String getNamespace(String message) {
        Pattern p = Pattern.compile("xmlns:(.*?)=");
        Matcher m = p.matcher(message);
        boolean found = m.find();

        return found ? m.group(1) : "";
    }
}
