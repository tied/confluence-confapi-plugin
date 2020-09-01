package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.confluence.model.PermissionAnonymousAccessBean;
import de.aservo.confapi.confluence.service.api.PermissionsService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

import static com.atlassian.confluence.security.SpacePermission.BROWSE_USERS_PERMISSION;
import static com.atlassian.confluence.security.SpacePermission.USE_CONFLUENCE_PERMISSION;

@Component
@ExportAsService(PermissionsService.class)
public class PermissionsServiceImpl implements PermissionsService {

    private final AnonymousUserPermissionsService anonymousUserPermissionsService;
    private final SpacePermissionManager spacePermissionManager;

    @Inject
    public PermissionsServiceImpl(
            @ComponentImport AnonymousUserPermissionsService anonymousUserPermissionsService,
            @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.anonymousUserPermissionsService = anonymousUserPermissionsService;
        this.spacePermissionManager = spacePermissionManager;
    }

    @Override
    public PermissionAnonymousAccessBean getPermissionAnonymousAccess() {
        List<SpacePermission> globalPermissions = spacePermissionManager.getGlobalPermissions();
        PermissionAnonymousAccessBean accessBean = new PermissionAnonymousAccessBean();
        accessBean.setAllowForPages(containsAnonymousPermission(globalPermissions, USE_CONFLUENCE_PERMISSION));
        accessBean.setAllowForUserProfiles(containsAnonymousPermission(globalPermissions, BROWSE_USERS_PERMISSION));
        return accessBean;
    }

    @Override
    public PermissionAnonymousAccessBean setPermissionAnonymousAccess(PermissionAnonymousAccessBean accessBean) {
        if (accessBean.getAllowForPages() != null) {
            anonymousUserPermissionsService.setUsePermission(accessBean.getAllowForPages());
        }
        if (accessBean.getAllowForUserProfiles() != null) {
            anonymousUserPermissionsService.setViewUserProfilesPermission(accessBean.getAllowForUserProfiles());
        }
        return getPermissionAnonymousAccess();
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
