package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.model.UserDirectoryBean;
import de.aservo.atlassian.confapi.service.UserDirectoryService;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.aservo.atlassian.confluence.confapi.util.Constants.API_VERSION;
import static de.aservo.atlassian.confluence.confapi.util.Constants.HTTP_BASIC_SCHEME_NAME;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * The type User directory resource.
 */
@Path(ConfAPI.DIRECTORIES)
@Produces(MediaType.APPLICATION_JSON)
@ResourceFilters(AdminOnlyResourceFilter.class)
@OpenAPIDefinition(
        info = @Info(
            title = "ConfAPI for Confluence",
            description = "This resource provides methods for accessing user directory configuration for Confluence.",
            version = API_VERSION,
            contact = @Contact(url = "https://github.com/aservo/confluence-confapi-plugin", email = "github@aservo.com")
        ),
        security = @SecurityRequirement(name = HTTP_BASIC_SCHEME_NAME)
)
@SecurityScheme(name = HTTP_BASIC_SCHEME_NAME, type = HTTP)
@Component
public class UserDirectoryResource {

    private static final Logger log = LoggerFactory.getLogger(UserDirectoryResource.class);

    private final UserDirectoryService directoryService;

    /**
     * Instantiates a new User directory resource.
     *
     * @param directoryService the crowd directory service
     */
    @Inject
    public UserDirectoryResource(UserDirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    /**
     * Gets directories.
     *
     * @return the directories
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Retrieves user directory information",
            description = "Upon successful request, returns a list of UserDirectoryBean object containing user directory details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "user directory details list", content = @Content(schema = @Schema(implementation = UserDirectoryBean.class))),
                    @ApiResponse(responseCode = "400", description = "An error occurred while retrieving the user directory list")
            })
    public Response getDirectories() {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            List<UserDirectoryBean> directories = directoryService.getDirectories();
            return Response.ok(directories).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

    /**
     * Add directory.
     *
     * @param testConnection the test connection
     * @param directory      the directory
     * @return the response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Adds a new user directory",
            description = "Upon successful request, returns the added UserDirectoryBean object, Any existing configurations with the same name property are removed before adding the new configuration",
            responses = {
                    @ApiResponse(responseCode = "200", description = "user directory added", content = @Content(schema = @Schema(implementation = UserDirectoryBean.class))),
                    @ApiResponse(responseCode = "400", description = "An error occured while setting adding the new user directory")
            })
    public Response addDirectory(@Parameter(description = "Whether or not to test the connection to the user directory service, e.g. CROWD (null defaults to TRUE)", schema = @Schema(implementation = Boolean.class)) @QueryParam("test") Boolean testConnection,
                                 @RequestBody(description = "The user directory to add", required = true, content = @Content(schema = @Schema(implementation = UserDirectoryBean.class))) UserDirectoryBean directory) {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            boolean test = testConnection == null || Boolean.TRUE.equals(testConnection);
            UserDirectoryBean addDirectory = directoryService.addDirectory(directory, test);
            return Response.ok(addDirectory).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }
}
