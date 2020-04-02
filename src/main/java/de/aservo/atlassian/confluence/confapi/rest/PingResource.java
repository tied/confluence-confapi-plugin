package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ConfAPI.PING)
@AnonymousAllowed
@Produces({MediaType.APPLICATION_JSON})
public class PingResource {

    public static final String PONG = "pong";

    @GET
    @Operation(
            tags = { ConfAPI.PING },
            summary = "Simple ping method for probing the REST api. Returns 'pong' upon success",
            responses = {
                    @ApiResponse(responseCode = "200"),
            }
    )
    public Response getPing() {
        return Response.ok(PONG).build();
    }

}
