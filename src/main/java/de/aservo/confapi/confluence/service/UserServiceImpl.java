package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.NotFoundException;
import de.aservo.confapi.commons.model.UserBean;
import de.aservo.confapi.commons.service.api.UserService;
import de.aservo.confapi.confluence.model.util.UserBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static de.aservo.confapi.commons.util.BeanValidationUtil.validate;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Component
@ExportAsService(UserService.class)
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserManager userManager;

    @Inject
    public UserServiceImpl(@ComponentImport final UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public UserBean getUser(
            final String userName) throws NotFoundException {

        final User user = findConfluenceUser(userName);
        return UserBeanUtil.toUserBean(user);
    }

    @Override
    public UserBean updateUser(
            final String userName,
            final UserBean userBean) throws NotFoundException, BadRequestException {

        validate(userBean);
        final User user = findConfluenceUser(userName);

        // userManager.saveUser will convert this user into a ConfluenceUser
        final DefaultUser updateUser = new DefaultUser(user);

        if (isNotBlank(userBean.getUserName())) {
            log.info("Updating user name is currently not supported");
        }
        if (isNotBlank(userBean.getFullName())) {
            updateUser.setFullName(userBean.getFullName());
        }
        if (isNotBlank(userBean.getEmail())) {
            updateUser.setEmail(userBean.getEmail());
        }
        if (isNotBlank(userBean.getPassword())) {
            updatePassword(userName, userBean.getPassword());
        }

        try {
            userManager.saveUser(updateUser);
        } catch (EntityException e) {
            throw new BadRequestException(String.format("User %s cannot be updated", userName));
        }

        return UserBeanUtil.toUserBean(updateUser);
    }

    @Override
    public UserBean updatePassword(
            final String userName,
            final String password) throws NotFoundException, BadRequestException {

        final User user = findConfluenceUser(userName);

        try {
            userManager.alterPassword(user, password);
        } catch (EntityException e) {
            throw new BadRequestException(String.format("Password for user %s cannot be set", userName));
        }

        return UserBeanUtil.toUserBean(user);
    }

    private User findConfluenceUser(
            final String userName) throws NotFoundException {

        final ConfluenceUser confluenceUser;

        try {
            // user *should* always be ConfluenceUser if not null
            confluenceUser = (ConfluenceUser) userManager.getUser(userName);
        } catch (EntityException | ClassCastException e) {
            throw new NotFoundException(String.format("User %s cannot be found", userName));
        }

        return confluenceUser;
    }

}
