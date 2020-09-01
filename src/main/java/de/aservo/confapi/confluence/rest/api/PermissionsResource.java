package de.aservo.confapi.confluence.rest.api;

import de.aservo.confapi.confluence.model.PermissionAnonymousAccessBean;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.model.ErrorCollection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public interface PermissionsResource {

    @GET
    @Path(ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
    @Operation(
            tags = { ConfAPI.PERMISSIONS },
            summary = "Retrieve current anonymous access configuration",
            description = "Gets the current global permissions for anonymous access to public pages and user profiles",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PermissionAnonymousAccessBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    Response getPermissionAnonymousAccess();

    @PUT
    @Path(ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
    @Operation(
            tags = { ConfAPI.PERMISSIONS },
            summary = "Set anonymous access configuration",
            description = "Sets global permissions for anonymous access to public pages and user profiles",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PermissionAnonymousAccessBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    Response setPermissionAnonymousAccess(
            @NotNull PermissionAnonymousAccessBean accessBean);

}
