package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import de.aservo.atlassian.confluence.confapi.model.PermissionAnonymousAccessBean;
import de.aservo.atlassian.confluence.confapi.rest.api.PermissionsResource;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.model.ErrorCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
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
public class PermissionsResourceImpl implements PermissionsResource {

    private static final Logger log = LoggerFactory.getLogger(PermissionsResourceImpl.class);

    private final AnonymousUserPermissionsService anonymousUserPermissionsService;
    private final SpacePermissionManager spacePermissionManager;

    /**
     * Instantiates a new Global permissions resource.
     *
     * @param anonymousUserPermissionsService the anonymous user permissions service
     */
    @Inject
    public PermissionsResourceImpl(@ComponentImport AnonymousUserPermissionsService anonymousUserPermissionsService,
                               @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.anonymousUserPermissionsService = anonymousUserPermissionsService;
        this.spacePermissionManager = spacePermissionManager;
    }

    /**
     * Gets the global access permissions.
     *
     * @return the global access permissions
     */
    @Override
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
    @Override
    public Response setPermissionAnonymousAccess(
            @NotNull final PermissionAnonymousAccessBean accessBean) {

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
