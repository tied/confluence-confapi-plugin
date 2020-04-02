package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.model.DirectoryBean;
import de.aservo.atlassian.confapi.service.DirectoryService;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path(ConfAPI.DIRECTORIES)
@Produces(MediaType.APPLICATION_JSON)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class DirectoryResource {

    private static final Logger log = LoggerFactory.getLogger(DirectoryResource.class);

    private final DirectoryService directoryService;

    @Inject
    public DirectoryResource(DirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    @GET
    @Operation(
            summary = "Get the list of user directories",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DirectoryBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    public Response getDirectories() {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            List<DirectoryBean> directories = directoryService.getDirectories();
            return Response.ok(directories).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Add a new directory",
            description = "Any existing directory with the same name will be removed before adding the new one",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DirectoryBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    public Response addDirectory(
            @QueryParam("testConnection") boolean testConnection,
            final DirectoryBean directory) {

        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            DirectoryBean addDirectory = directoryService.addDirectory(directory, testConnection);
            return Response.ok(addDirectory).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

}
