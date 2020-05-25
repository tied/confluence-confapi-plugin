package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.rest.AbstractMailServerResourceImpl;
import de.aservo.atlassian.confapi.service.api.MailServerService;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.MAIL_SERVER)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class MailServerResourceImpl extends AbstractMailServerResourceImpl {

    @Inject
    public MailServerResourceImpl(MailServerService mailServerService) {
        super(mailServerService);
    }
}
