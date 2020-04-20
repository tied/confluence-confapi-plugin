package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.rest.api.PingResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ConfAPI.PING)
@AnonymousAllowed
@Produces(MediaType.TEXT_PLAIN)
public class PingResourceImpl implements PingResource {

    @GET
    @Override
    public Response getPing() {
        return Response.ok(PONG).build();
    }

}
