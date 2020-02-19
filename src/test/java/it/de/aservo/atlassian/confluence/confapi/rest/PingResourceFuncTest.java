package it.de.aservo.atlassian.confluence.confapi.rest;

import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PingResourceFuncTest {

    @Test
    public void testPing() {
        final String baseUrl = System.getProperty("baseurl");
        final String resourceUrl = baseUrl + "/rest/confapi/1/ping";

        final RestClient client = new RestClient();
        final Resource resource = client.resource(resourceUrl);

        assertEquals(200, resource.get().getStatusCode());
    }

}
