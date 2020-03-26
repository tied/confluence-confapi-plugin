package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import de.aservo.atlassian.confluence.confapi.model.UserBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserManager userManager;

    private UserService userService;

    @Before
    public void setup() {
        userService = new UserService(userManager);
    }

    @Test
    public void testGetUser() throws EntityException, UserNotFoundException {
        User user = createUser();
        doReturn(user).when(userManager).getUser(user.getName());

        UserBean userBean = userService.getUser(user.getName());

        assertEquals(userBean, new UserBean(user));
        assertNull(userBean.getPassword());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserIsNotConfluenceUser() throws EntityException, UserNotFoundException {
        User user = new DefaultUser("test", "test user", "user@user.de");

        doReturn(user).when(userManager).getUser(user.getName());

        userService.getUser(user.getName());
    }

    @Test
    public void testUpdateUser() throws EntityException, UserNotFoundException, IllegalAccessException {
        User user = createUser();
        UserBean userBeanUpdate = new UserBean(user);

        doReturn(user).when(userManager).getUser(user.getName());

        UserBean userBean = userService.updateEmail(userBeanUpdate);

        assertEquals(userBean, userBeanUpdate);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdateUserNotConfluenceUser() throws EntityException, UserNotFoundException, IllegalAccessException {
        User user = new DefaultUser("test", "test user", "user@user.de");
        UserBean userBeanUpdate = new UserBean(user);

        doReturn(user).when(userManager).getUser(user.getName());

        userService.updateEmail(userBeanUpdate);
    }

    @Test
    public void testUpdateExceptionOnWriteEmailField() throws EntityException, UserNotFoundException, IllegalAccessException {
        User user = createUser();
        UserBean userBean = new UserBean(user);
        ConfluenceUserImpl confluenceUser = (ConfluenceUserImpl) user;

        doReturn(user).when(userManager).getUser(user.getName());
        doThrow(new RuntimeException()).when(userManager).saveUser(confluenceUser);

        UserBean userBeanUpdated = userService.updateEmail(userBean);

        assertEquals(userBeanUpdated, userBean);
    }

    @Test
    public void testUpdateUserPassword() throws EntityException, UserNotFoundException {
        User user = createUser();
        UserBean userBeanUpdate = new UserBean(user);
        doReturn(user).when(userManager).getUser(user.getName());

        UserBean userBean = userService.updatePassword(user.getName(), "newPW");

        assertEquals(userBean, userBeanUpdate);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUpdateUserPasswordNotConfluenceUser() throws EntityException, UserNotFoundException {
        User user = new DefaultUser("test", "test user", "user@user.de");

        doReturn(user).when(userManager).getUser(user.getName());

        userService.updatePassword(user.getName(), "newPW");
    }

    private User createUser() {
        return new ConfluenceUserImpl("test", "test user", "user@user.de");
    }
}
