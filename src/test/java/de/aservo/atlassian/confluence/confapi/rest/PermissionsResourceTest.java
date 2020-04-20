package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.service.AnonymousUserPermissionsService;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confluence.confapi.model.PermissionAnonymousAccessBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.atlassian.confluence.security.SpacePermission.USE_CONFLUENCE_PERMISSION;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsResourceTest {

    private PermissionsResourceImpl resource;

    @Mock
    private AnonymousUserPermissionsService anonymousUserPermissionsService;

    @Mock
    private SpacePermissionManager spacePermissionManager;

    @Before
    public void setup() {
        resource = new PermissionsResourceImpl(anonymousUserPermissionsService, spacePermissionManager);
    }

    @Test
    public void testGetAnonymousPermissions() {
        List<SpacePermission> globalPermissions = new ArrayList<>();
        globalPermissions.add(SpacePermission.createGroupSpacePermission(USE_CONFLUENCE_PERMISSION, null, null));

        doReturn(globalPermissions).when(spacePermissionManager).getGlobalPermissions();

        Response response = resource.getPermissionAnonymousAccess();
        assertEquals(200, response.getStatus());
        assertEquals(PermissionAnonymousAccessBean.class, response.getEntity().getClass());

        PermissionAnonymousAccessBean accessBean = (PermissionAnonymousAccessBean)response.getEntity();
        assertTrue(accessBean.getAllowForPages());
        assertFalse(accessBean.getAllowForUserProfiles());
    }

    @Test
    public void testGetAnonymousPermissionsWithError() {
        doThrow(new RuntimeException()).when(spacePermissionManager).getGlobalPermissions();

        Response response = resource.getPermissionAnonymousAccess();
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    @Test
    public void testSetAnonymousPermissions() {
        PermissionAnonymousAccessBean accessBean = new PermissionAnonymousAccessBean(true, true);
        Response response = resource.setPermissionAnonymousAccess(accessBean);
        assertEquals(200, response.getStatus());
        assertEquals(PermissionAnonymousAccessBean.class, response.getEntity().getClass());
    }

    @Test
    public void testSetAnonymousPermissionsWithError() {
        doThrow(new RuntimeException()).when(anonymousUserPermissionsService).setUsePermission(true);

        PermissionAnonymousAccessBean accessBean = new PermissionAnonymousAccessBean(true, true);
        Response response = resource.setPermissionAnonymousAccess(accessBean);
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }
}
