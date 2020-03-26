package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import de.aservo.atlassian.confluence.confapi.model.UserBean;
import de.aservo.atlassian.confluence.confapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * The type User resource.
 */
@Path(ConfAPI.USERS)
@Produces(MediaType.APPLICATION_JSON)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class UserResource {

    private static final Logger log = LoggerFactory.getLogger(UserResource.class);

    private final UserService userService;

    /**
     * Instantiates a new User resource.
     *
     * @param userService the user service
     */
    @Inject
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * Gets user.
     *
     * @param username the username
     * @return the user
     */
    @GET
    @Operation(summary = "Retrieves user information",
            tags = { ConfAPI.USERS },
            description = "Upon successful request, returns a `UserBean` object containing user details",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            })
    public Response getUser(@QueryParam("username") String username) {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            return Response.ok(userService.getUser(username)).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

    /**
     * Update user.
     *
     * @param user the userbean to update
     * @return the response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Operation(summary = "Updates user details",
            tags = { ConfAPI.USERS },
            description = "NOTE: Currently only the email address is updated from the provided `UserBean` property. Upon successful request, returns the updated `UserBean` object",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            })
    public Response updateUser(UserBean user) {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            return Response.ok(userService.updateEmail(user)).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

    /**
     * Update user password.
     *
     * @param user the userbean to update holding the new password
     * @return the response
     */
    @PUT
    @Path("/password")
    @Operation(summary = "Updates the user password",
            tags = { ConfAPI.USERS },
            description = "Upon successful request, returns the updated `UserBean` object.",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            })
    public Response updateUserPassword(UserBean user) {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            return Response.ok(userService.updatePassword(user.getUserName(), user.getPassword())).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }
}
