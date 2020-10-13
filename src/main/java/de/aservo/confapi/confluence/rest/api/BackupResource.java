package de.aservo.confapi.confluence.rest.api;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.model.ErrorCollection;
import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

public interface BackupResource {

    @GET
    @Path(ConfAPI.BACKUP_EXPORT)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = { ConfAPI.BACKUP },
            summary = "Export based on given configuration",
            description = "Initiates an asynchronous export if enabled by server, synchronous otherwise",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Synchronous export, the download URL will be returned in the location header"),
                    @ApiResponse(responseCode = "202", description = "Asynchronous export, the queue URL will be returned in the location header"),
                    @ApiResponse(content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    Response getExport(
            @QueryParam("force-synchronous") @DefaultValue("false") final boolean forceSynchronous,
            @Nonnull final BackupBean backupBean);

    @GET
    @Path(ConfAPI.BACKUP_EXPORT + "/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = { ConfAPI.BACKUP },
            summary = "Export based on given key",
            description = "Same as export but especially for the most common case: Single space export with attachments and comments",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Synchronous export, the download URL will be returned in the location header"),
                    @ApiResponse(responseCode = "202", description = "Asynchronous export, the queue URL will be returned in the location header"),
                    @ApiResponse(content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    Response getExportByKey(
            @QueryParam("force-synchronous") @DefaultValue("false") final boolean forceSynchronous,
            @Nonnull @PathParam("key") String key);

    @POST
    @XsrfProtectionExcluded
    @Path(ConfAPI.BACKUP_IMPORT)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = { ConfAPI.BACKUP },
            summary = "Import based on an export file upload",
            description = "Initiates an asynchronous import if enabled by server, synchronous otherwise",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Synchronous import"),
                    @ApiResponse(responseCode = "202", description = "Asynchronous import, the queue URL will be returned in the location header"),
                    @ApiResponse(content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    Response doImportByFileUpload(
            @Nonnull @MultipartFormParam("file") final FilePart filePart);

    @GET
    @Path(ConfAPI.BACKUP_QUEUE + "/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            tags = { ConfAPI.BACKUP },
            summary = "Information about an initiated export / import task",
            description = "Returns information like ETA if task is still running or information about the created object if completed",
            responses = {
                    @ApiResponse(
                            responseCode = "200", content = @Content(schema = @Schema(implementation = BackupQueueBean.class)),
                            description = "Task is still running, return information like ETA"
                    ),
                    @ApiResponse(
                            responseCode = "201", content = @Content(schema = @Schema(implementation = BackupQueueBean.class)),
                            description = "Task completed, return download URL in the location header (export only)"
                    ),
                    @ApiResponse(responseCode = "404", description = "No task found for the given UUID"),
                    @ApiResponse(content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            }
    )
    Response getQueue(
            @Nonnull @PathParam("uuid") final UUID uuid);

}
