package it.de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

@RunWith(AtlassianPluginsTestRunner.class)
public class PingResourceFuncTest {

    final String baseUrl = System.getProperty("baseurl");
    final String resourceUrl = baseUrl + "/rest/confapi/1/" + ConfAPI.PING;

    private RestClient client = new RestClient();
    private Resource resource = client.resource(resourceUrl);

    @Test
    public void testGetPing() {
        assertEquals(Status.OK.getStatusCode(), resource.get().getStatusCode());
    }

}
