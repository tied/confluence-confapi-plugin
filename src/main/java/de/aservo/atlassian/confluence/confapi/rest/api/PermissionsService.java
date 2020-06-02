package de.aservo.atlassian.confluence.confapi.rest.api;

import de.aservo.atlassian.confluence.confapi.model.PermissionAnonymousAccessBean;

import javax.validation.constraints.NotNull;

public interface PermissionsService {

    /**
     * Returns the currently configured anonymous access permissions
     *
     * @return
     */
    PermissionAnonymousAccessBean getPermissionAnonymousAccess();

    /**
     * Sets the anonymous access permissions
     *
     * @param accessBean - the config to set
     * @return the updated anonymous access permissions
     */
    PermissionAnonymousAccessBean setPermissionAnonymousAccess(@NotNull PermissionAnonymousAccessBean accessBean);
}
