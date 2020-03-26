package de.aservo.atlassian.confluence.confapi.model;

import com.atlassian.confluence.user.ConfluenceUserImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UserBeanTest {

    @Test
    public void testParameterConstructor() {
        ConfluenceUserImpl user = new ConfluenceUserImpl("test", "test user", "user@user.de");
        UserBean bean = new UserBean(user);

        assertNotNull(bean);
        assertEquals(bean.getUserName(), user.getName());
        assertEquals(bean.getFullName(), user.getFullName());
        assertEquals(bean.getEmail(), user.getEmail());
        assertNull(bean.getPassword());
    }
}
