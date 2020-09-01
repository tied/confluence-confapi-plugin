package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import de.aservo.confapi.confluence.model.PermissionAnonymousAccessBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.atlassian.confluence.security.SpacePermission.USE_CONFLUENCE_PERMISSION;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsServiceTest {

    @Mock
    private AnonymousUserPermissionsService anonymousUserPermissionsService;

    @Mock
    private SpacePermissionManager spacePermissionManager;

    private PermissionsServiceImpl permissionsService;

    @Before
    public void setup() {
        permissionsService = new PermissionsServiceImpl(anonymousUserPermissionsService, spacePermissionManager);
    }

    @Test
    public void testGetAnonymousPermissions() {
        List<SpacePermission> globalPermissions = new ArrayList<>();
        globalPermissions.add(SpacePermission.createGroupSpacePermission(USE_CONFLUENCE_PERMISSION, null, null));

        doReturn(globalPermissions).when(spacePermissionManager).getGlobalPermissions();

        PermissionAnonymousAccessBean response = permissionsService.getPermissionAnonymousAccess();
        assertNotNull(response);
    }

    @Test
    public void testSetAnonymousPermissions() {
        PermissionAnonymousAccessBean accessBean = new PermissionAnonymousAccessBean(true, true);
        PermissionAnonymousAccessBean response = permissionsService.setPermissionAnonymousAccess(accessBean);
        assertNotNull(response);
    }
}
