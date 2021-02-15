package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.NotFoundException;
import de.aservo.confapi.commons.model.UserBean;
import de.aservo.confapi.commons.service.api.UsersService;
import de.aservo.confapi.confluence.model.util.UserBeanUtil;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static de.aservo.confapi.commons.util.BeanValidationUtil.validate;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Component
@ExportAsService(UsersService.class)
public class UsersServiceImpl implements UsersService {

    private final UserManager userManager;

    private final UserAccessor userAccessor;

    @Inject
    public UsersServiceImpl(
            @ComponentImport final UserManager userManager,
            @ComponentImport final UserAccessor userAccessor) {

        this.userManager = userManager;
        this.userAccessor = userAccessor;
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
            final UserBean userBean) {

        validate(userBean);
        User user = findConfluenceUser(userName);

        if (isNotBlank(userBean.getUsername()) && !userName.equals(userBean.getUsername())) {
            try {
                user = userAccessor.renameUser((ConfluenceUser)user, userBean.getUsername());
            } catch (EntityException e) {
                throw new BadRequestException(String.format("Error trying to update username for user: %s. " +
                        "New username might already exist, operation is not permitted or the crowd service could not " +
                        "handle the request.", user.getName()));
            }
        }
        // userManager.saveUser will convert this user into a ConfluenceUser
        final DefaultUser updateUser = new DefaultUser(user);

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
            final String username,
            final String password) throws NotFoundException, BadRequestException {

        final User user = findConfluenceUser(username);

        try {
            userManager.alterPassword(user, password);
        } catch (EntityException e) {
            throw new BadRequestException(String.format("Password for user %s cannot be set", username));
        }

        return UserBeanUtil.toUserBean(user);
    }

    private User findConfluenceUser(
            final String username) {

        final ConfluenceUser confluenceUser;

        try {
            // user *should* always be ConfluenceUser if not null
            confluenceUser = (ConfluenceUser) userManager.getUser(username);
        } catch (EntityException | ClassCastException e) {
            throw new NotFoundException(String.format("User %s cannot be found", username));
        }

        return confluenceUser;
    }

}
