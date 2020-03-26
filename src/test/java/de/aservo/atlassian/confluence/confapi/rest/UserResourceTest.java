package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.user.EntityException;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confluence.confapi.model.UserBean;
import de.aservo.atlassian.confluence.confapi.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class UserResourceTest {

    @Mock
    private UserService userService;

    private UserResource resource;

    @Before
    public void setup() {
        resource = new UserResource(userService);
    }

    @Test
    public void testGetUser() throws EntityException, UserNotFoundException {
        ConfluenceUser user = new ConfluenceUserImpl("test", "test user", "user@user.de");
        UserBean bean = new UserBean(user);

        doReturn(bean).when(userService).getUser(bean.getUserName());

        final Response response = resource.getUser(bean.getUserName());
        assertEquals(200, response.getStatus());
        final UserBean userBean = (UserBean) response.getEntity();

        assertEquals(userBean, bean);
    }

    @Test
    public void testGetUserWithError() throws EntityException, UserNotFoundException {
        doThrow(new UserNotFoundException("user")).when(userService).getUser(any(String.class));

        final Response response = resource.getUser("user");
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    @Test
    public void testUpdateUser() throws EntityException, UserNotFoundException, IllegalAccessException {
        ConfluenceUserImpl user = new ConfluenceUserImpl("test", "test user", "user@user.de");
        UserBean bean = new UserBean(user);

        doReturn(bean).when(userService).updateEmail(bean);

        final Response response = resource.updateUser(bean);
        assertEquals(200, response.getStatus());
        final UserBean userBean = (UserBean) response.getEntity();

        assertEquals(userBean, bean);
    }

    @Test
    public void testUpdateUserWithError() throws EntityException, UserNotFoundException, IllegalAccessException {
        ConfluenceUserImpl user = new ConfluenceUserImpl("test", "test user", "user@user.de");
        UserBean bean = new UserBean(user);

        doThrow(new UserNotFoundException("user")).when(userService).updateEmail(any(UserBean.class));

        final Response response = resource.updateUser(bean);
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    @Test
    public void testUpdateUserPassword() throws EntityException, UserNotFoundException {
        ConfluenceUserImpl user = new ConfluenceUserImpl("test", "test user", "user@user.de");
        UserBean bean = new UserBean(user);
        bean.setPassword("newPW");

        doReturn(bean).when(userService).updatePassword(bean.getUserName(), "newPW");

        final Response response = resource.updateUserPassword(bean);
        assertEquals(200, response.getStatus());
        final UserBean userBean = (UserBean) response.getEntity();

        assertEquals(userBean, bean);
    }

    @Test
    public void testUpdateUserPasswordWithError() throws EntityException, UserNotFoundException {
        ConfluenceUserImpl user = new ConfluenceUserImpl("test", "test user", "user@user.de");
        UserBean bean = new UserBean(user);
        bean.setPassword("newPW");

        doThrow(new UserNotFoundException("user")).when(userService).updatePassword(any(String.class), any(String.class));

        final Response response = resource.updateUserPassword(bean);
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }
}
