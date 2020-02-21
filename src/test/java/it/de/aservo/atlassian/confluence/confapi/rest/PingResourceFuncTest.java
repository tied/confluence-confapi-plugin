package it.de.aservo.atlassian.confluence.confapi.rest;

import de.aservo.atlassian.confapi.constants.ConfAPI;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class PingResourceFuncTest {

    final String baseUrl = System.getProperty("baseurl");
    final String resourceUrl = baseUrl + "/rest/confapi/1/" + ConfAPI.PING;

    private RestClient client = new RestClient();
    private Resource resource = client.resource(resourceUrl);

    @Test
    public void testGetPing() {
        assertEquals(Response.Status.OK.getStatusCode(), resource.get().getStatusCode());
    }

}
