package de.aservo.atlassian.confluence.confapi.rest;

import de.aservo.atlassian.confapi.constants.ConfAPI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static de.aservo.atlassian.confapi.junit.ResourceAssert.assertResourceMethodGetNoSubPath;
import static de.aservo.atlassian.confapi.junit.ResourceAssert.assertResourcePath;
import static de.aservo.atlassian.confluence.confapi.rest.PingResource.PONG;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PingResourceTest {

    private PingResource pingResource;

    @Before
    public void setup() {
        pingResource = new PingResource();
    }

    @Test
    public void testResourcePath() {
        assertResourcePath(pingResource, ConfAPI.PING);
    }

    @Test
    public void testGetPingPath() {
        assertResourceMethodGetNoSubPath(pingResource, "getPing");
    }

    @Test
    public void testGetPing() {
        final Response pingResponse = pingResource.getPing();
        assertEquals(PONG, pingResponse.getEntity().toString());
    }

}
