package de.aservo.atlassian.confluence.confapi.rest;

import de.aservo.atlassian.confluence.confapi.model.PermissionAnonymousAccessBean;
import de.aservo.atlassian.confluence.confapi.rest.api.PermissionsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsResourceTest {

    private PermissionsResourceImpl resource;

    @Mock
    private PermissionsService permissionsService;

    @Before
    public void setup() {
        resource = new PermissionsResourceImpl(permissionsService);
    }

    @Test
    public void testGetAnonymousPermissions() {
        doReturn(PermissionAnonymousAccessBean.EXAMPLE_1).when(permissionsService).getPermissionAnonymousAccess();

        Response response = resource.getPermissionAnonymousAccess();
        assertEquals(200, response.getStatus());
        assertEquals(PermissionAnonymousAccessBean.class, response.getEntity().getClass());

        PermissionAnonymousAccessBean accessBean = (PermissionAnonymousAccessBean)response.getEntity();
        assertEquals(PermissionAnonymousAccessBean.EXAMPLE_1, accessBean);
    }

    @Test
    public void testSetAnonymousPermissions() {
        doReturn(PermissionAnonymousAccessBean.EXAMPLE_1).when(permissionsService).getPermissionAnonymousAccess();
        Response response = resource.setPermissionAnonymousAccess(PermissionAnonymousAccessBean.EXAMPLE_1);
        assertEquals(200, response.getStatus());
    }
}
