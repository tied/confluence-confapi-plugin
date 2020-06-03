package de.aservo.atlassian.confluence.confapi.model.util;

import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.user.User;
import de.aservo.confapi.commons.model.UserBean;

public class UserBeanUtil {

    public static UserBean toUserBean(
            final User user) {

        final UserBean userBean = new UserBean();
        userBean.setUserName(user.getName());
        userBean.setFullName(user.getFullName());
        userBean.setEmail(user.getEmail());

        return userBean;
    }

    // Make sure to return a ConfluenceUser here to that unit tests are working
    public static User toUser(
            final UserBean userBean) {

        return new ConfluenceUserImpl(
                userBean.getUserName(),
                userBean.getFullName(),
                userBean.getEmail()
        );
    }

    private UserBeanUtil() {
    }

}
