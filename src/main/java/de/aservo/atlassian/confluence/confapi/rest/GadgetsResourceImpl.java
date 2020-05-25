package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.rest.AbstractGadgetsResourceImpl;
import de.aservo.atlassian.confapi.service.api.GadgetsService;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.GADGETS)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class GadgetsResourceImpl extends AbstractGadgetsResourceImpl {

    @Inject
    public GadgetsResourceImpl(GadgetsService gadgetsService) {
        super(gadgetsService);
    }
}
