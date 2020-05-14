package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.rest.AbstractUsersResourceImpl;
import de.aservo.atlassian.confapi.service.api.UserService;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.USERS)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class UsersResourceImpl extends AbstractUsersResourceImpl {

    @Inject
    public UsersResourceImpl(UserService userService) {
        super(userService);
    }

    // Completely inhering the implementation of AbstractUserResourceImpl

}
