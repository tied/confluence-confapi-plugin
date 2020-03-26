package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.crowd.embedded.atlassianuser.EmbeddedCrowdUser;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import de.aservo.atlassian.confluence.confapi.model.UserBean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static de.aservo.atlassian.confapi.util.BeanValidationUtil.validate;

/**
 * The type User service.
 */
@Component
@ExportAsService({UserService.class})
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserManager userManager;

    /**
     * Instantiates a new User service.
     *
     * @param userManager the userBean manager
     */
    public UserService(@ComponentImport UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Gets userBean.
     *
     * @param username the username
     * @return the userBean
     * @throws EntityException       the entity exception
     * @throws UserNotFoundException the userBean not found exception
     */
    public UserBean getUser(String username) throws EntityException, UserNotFoundException {
        User user = userManager.getUser(username);
        if (user instanceof ConfluenceUser) {
            return new UserBean(user);
        } else {
            throw new UserNotFoundException(username);
        }
    }

    /**
     * Update userBean.
     *
     * @param userBean the userBean
     * @return the userBean
     * @throws EntityException        the entity exception
     * @throws IllegalAccessException the illegal access exception
     * @throws UserNotFoundException  the userBean not found exception
     */
    public UserBean updateEmail(UserBean userBean) throws EntityException, UserNotFoundException, IllegalAccessException {
        validate(userBean);
        User user = userManager.getUser(userBean.getUserName());
        if (user instanceof ConfluenceUser) {
            String emailFieldName = "email";
            ConfluenceUserImpl confluenceUser = (ConfluenceUserImpl) user;
            try {
                //email field can only be modified through reflection because it is private
                FieldUtils.writeDeclaredField(confluenceUser, emailFieldName, userBean.getEmail(), true);
                userManager.saveUser(confluenceUser);
            } catch (Exception e) {
                //field "email" is only available from v6.15.10, for backwards compatibility try with backingUser
                User backingUser = confluenceUser.getBackingUser();
                if (backingUser instanceof EmbeddedCrowdUser) {
                    emailFieldName = "emailAddress";
                }
                log.debug("did not find field 'email' in class {}, trying with field '{}' of backingUser {} ...", confluenceUser.getClass(), emailFieldName, backingUser.getClass());
                FieldUtils.writeDeclaredField(backingUser, emailFieldName, userBean.getEmail(), true);
                userManager.saveUser(backingUser);
            }
            return getUser(userBean.getUserName());
        } else {
            throw new UserNotFoundException(userBean.getUserName());
        }
    }

    /**
     * Update userBean password.
     *
     * @param username the username
     * @param password the password
     * @return the userBean bean
     * @throws EntityException       the entity exception
     * @throws UserNotFoundException the userBean not found exception
     */
    public UserBean updatePassword(String username, String password) throws EntityException, UserNotFoundException {
        User user = userManager.getUser(username);
        if (user instanceof ConfluenceUser) {
            userManager.alterPassword(user, password);
            return getUser(username);
        } else {
            throw new UserNotFoundException(username);
        }
    }
}
