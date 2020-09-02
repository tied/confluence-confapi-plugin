package de.aservo.confapi.confluence.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.confluence.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractGadgetsResourceImpl;
import de.aservo.confapi.commons.service.api.GadgetsService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.GADGETS)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Component
public class GadgetsResourceImpl extends AbstractGadgetsResourceImpl {

    @Inject
    public GadgetsResourceImpl(GadgetsService gadgetsService) {
        super(gadgetsService);
    }
}
