package de.aservo.confapi.confluence.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.confluence.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractApplicationLinksResourceImpl;
import de.aservo.confapi.commons.service.api.ApplicationLinksService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.APPLICATION_LINKS)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Component
public class ApplicationLinksResourceImpl extends AbstractApplicationLinksResourceImpl {

    @Inject
    public ApplicationLinksResourceImpl(ApplicationLinksService applicationLinkService) {
        super(applicationLinkService);
    }
}
