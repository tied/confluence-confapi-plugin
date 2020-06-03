package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractGadgetsResourceImpl;
import de.aservo.confapi.commons.service.api.GadgetsService;
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
