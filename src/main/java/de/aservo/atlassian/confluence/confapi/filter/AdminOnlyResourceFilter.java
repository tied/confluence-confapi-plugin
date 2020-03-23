package de.aservo.atlassian.confluence.confapi.filter;

import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AuthenticationRequiredException;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.user.User;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

/**
 * The Admin only resource filter.
 */
@Provider
@Component
public class AdminOnlyResourceFilter implements ResourceFilter, ContainerRequestFilter {
    private final PermissionManager permissionManager;

    /**
     * Instantiates a new Admin only resource filter.
     *
     * @param permissionManager the permission manager
     */
    @Inject
    public AdminOnlyResourceFilter(@ComponentImport PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    public ContainerResponseFilter getResponseFilter() {
        return null;
    }

    public ContainerRequest filter(ContainerRequest containerRequest) {
        User loggedInUser = AuthenticatedUserThreadLocal.get();
        if (loggedInUser == null) {
            throw new AuthenticationRequiredException();
        } else if (!permissionManager.isConfluenceAdministrator(loggedInUser)) {
            throw new AuthorisationException("Client must be authenticated as an administrator to access this resource.");
        }
        return containerRequest;
    }
}
