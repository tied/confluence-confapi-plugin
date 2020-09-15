package it.de.aservo.confapi.confluence.rest;

import de.aservo.confapi.confluence.model.PermissionAnonymousAccessBean;
import de.aservo.confapi.commons.constants.ConfAPI;
import it.de.aservo.confapi.commons.rest.ResourceBuilder;
import org.apache.wink.client.ClientAuthenticationException;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PermissionsResourceFuncTest {

    @Test
    public void testGetAnonymousPermissions() {
        Resource permissionsResource = ResourceBuilder.builder(ConfAPI.PERMISSIONS + "/" + ConfAPI.PERMISSION_ANONYMOUS_ACCESS).build();

        ClientResponse clientResponse = permissionsResource.get();
        assertEquals(Response.Status.OK.getStatusCode(), clientResponse.getStatusCode());

        PermissionAnonymousAccessBean accessBean = clientResponse.getEntity(PermissionAnonymousAccessBean.class);
        assertNotNull(accessBean);
    }

    @Test
    public void testSetAnonymousPermissions() {
        Resource permissionsResource = ResourceBuilder.builder(ConfAPI.PERMISSIONS + "/" + ConfAPI.PERMISSION_ANONYMOUS_ACCESS).build();

        ClientResponse clientResponse = permissionsResource.put(getExampleBean());
        assertEquals(Response.Status.OK.getStatusCode(), clientResponse.getStatusCode());

        PermissionAnonymousAccessBean accessBean = clientResponse.getEntity(PermissionAnonymousAccessBean.class);
        assertEquals(getExampleBean(), accessBean);
    }

    @Test(expected = ClientAuthenticationException.class)
    public void testGetAnonymousPermissionsUnauthenticated() {
        Resource permissionsResource = ResourceBuilder.builder(ConfAPI.PERMISSIONS + "/" + ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
                .username("wrong")
                .password("password")
                .build();
        permissionsResource.get();
    }

    @Test(expected = ClientAuthenticationException.class)
    public void testSetAnonymousPermissionsUnauthenticated() {
        Resource permissionsResource = ResourceBuilder.builder(ConfAPI.PERMISSIONS + "/" + ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
                .username("wrong")
                .password("password")
                .build();
        permissionsResource.put(getExampleBean());
    }

    @Test
    @Ignore("cannot be executed because there is no default user with restricted access rights")
    public void testGetAnonymousPermissionsUnauthorized() {
        Resource permissionsResource = ResourceBuilder.builder(ConfAPI.PERMISSIONS + "/" + ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
                .username("user")
                .password("user")
                .build();
        permissionsResource.get();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), permissionsResource.put(getExampleBean()).getStatusCode());
    }

    @Test
    @Ignore("cannot be executed because there is no default user with restricted access rights")
    public void testSetAnonymousPermissionsUnauthorized() {
        Resource permissionsResource = ResourceBuilder.builder(ConfAPI.PERMISSIONS + "/" + ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
                .username("user")
                .password("user")
                .build();
        permissionsResource.put(getExampleBean());
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), permissionsResource.put(getExampleBean()).getStatusCode());
    }

    protected PermissionAnonymousAccessBean getExampleBean() {
        return PermissionAnonymousAccessBean.EXAMPLE_1;
    }
}
