package de.aservo.atlassian.confluence.confapi.model.util;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.user.User;
import de.aservo.confapi.commons.model.UserBean;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserBeanUtilTest {

    @Test
    public void testToUserBean() {
        final ConfluenceUser user = new ConfluenceUserImpl("user", "User Name", "user@localhost");
        final UserBean userBean = UserBeanUtil.toUserBean(user);

        assertEquals(user.getName(), userBean.getUserName());
        assertEquals(user.getFullName(), userBean.getFullName());
        assertEquals(user.getEmail(), userBean.getEmail());
    }

    @Test
    public void testToUser() {
        final UserBean userBean = UserBean.EXAMPLE_1;
        final User user = UserBeanUtil.toUser(userBean);

        assertEquals(userBean.getUserName(), user.getName());
        assertEquals(userBean.getFullName(), user.getFullName());
        assertEquals(userBean.getEmail(), user.getEmail());
    }

}
