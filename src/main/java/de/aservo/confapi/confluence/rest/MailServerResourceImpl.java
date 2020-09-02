package de.aservo.confapi.confluence.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.confluence.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractMailServerResourceImpl;
import de.aservo.confapi.commons.service.api.MailServerService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.MAIL_SERVER)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Component
public class MailServerResourceImpl extends AbstractMailServerResourceImpl {

    @Inject
    public MailServerResourceImpl(MailServerService mailServerService) {
        super(mailServerService);
    }
}
