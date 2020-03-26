package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import de.aservo.atlassian.confluence.confapi.model.PermissionAnonymousAccessBean;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.atlassian.confluence.security.SpacePermission.BROWSE_USERS_PERMISSION;
import static com.atlassian.confluence.security.SpacePermission.USE_CONFLUENCE_PERMISSION;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * The type Global permissions resource.
 */
@Path(ConfAPI.PERMISSIONS)
@Produces(MediaType.APPLICATION_JSON)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class PermissionsResource {

    private static final Logger log = LoggerFactory.getLogger(PermissionsResource.class);

    private final AnonymousUserPermissionsService anonymousUserPermissionsService;
    private final SpacePermissionManager spacePermissionManager;

    /**
     * Instantiates a new Global permissions resource.
     *
     * @param anonymousUserPermissionsService the anonymous user permissions service
     */
    @Inject
    public PermissionsResource(@ComponentImport AnonymousUserPermissionsService anonymousUserPermissionsService,
                               @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.anonymousUserPermissionsService = anonymousUserPermissionsService;
        this.spacePermissionManager = spacePermissionManager;
    }

    /**
     * Gets the global access permissions.
     *
     * @return the global access permissions
     */
    @GET
    @Path(ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
    @Operation(summary = "Retrieve current anonymous access configuration",
            tags = { ConfAPI.PERMISSIONS },
            description = "Gets the current global permissions for anonymous access to public pages and user profiles",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PermissionAnonymousAccessBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            })
    public Response getPermissionAnonymousAccess() {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            List<SpacePermission> globalPermissions = spacePermissionManager.getGlobalPermissions();
            PermissionAnonymousAccessBean accessBean = new PermissionAnonymousAccessBean();
            accessBean.setAllowForPages(containsAnonymousPermission(globalPermissions, USE_CONFLUENCE_PERMISSION));
            accessBean.setAllowForUserProfiles(containsAnonymousPermission(globalPermissions, BROWSE_USERS_PERMISSION));
            return Response.ok(accessBean).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

    /**
     * Sets global access permissions.
     *
     * @param accessBean          bean describing the anonymous access
     * @return the global access permissions
     */
    @PUT
    @Path(ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
    @Operation(summary = "Set anonymous access configuration",
            tags = { ConfAPI.PERMISSIONS },
            description = "Sets global permissions for anonymous access to public pages and user profiles",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = PermissionAnonymousAccessBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            })
    public Response setPermissionAnonymousAccess(PermissionAnonymousAccessBean accessBean) {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            if (accessBean.getAllowForPages() != null) {
                anonymousUserPermissionsService.setUsePermission(accessBean.getAllowForPages());
            }
            if (accessBean.getAllowForUserProfiles() != null) {
                anonymousUserPermissionsService.setViewUserProfilesPermission(accessBean.getAllowForUserProfiles());
            }
            return getPermissionAnonymousAccess();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(BAD_REQUEST).entity(errorCollection).build();
    }

    private boolean containsAnonymousPermission(List<SpacePermission> permissions, String permissionType) {
        for (SpacePermission permission : permissions) {
            if (permission.getType().equals(permissionType) && permission.getGroup() == null) {
                return true;
            }
        }
        return false;
    }
}
