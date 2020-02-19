package de.aservo.atlassian.confluence.confapi.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

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
    public void testPing() {
        final Response pingResponse = pingResource.get();
        assertEquals(PONG, pingResponse.getEntity().toString());
    }

}
