package de.aservo.confapi.confluence.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.confluence.filter.AdminOnlyResourceFilter;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractLicensesResourceImpl;
import de.aservo.confapi.commons.service.api.LicensesService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.LICENSES)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class LicencesResourceImpl extends AbstractLicensesResourceImpl {

    @Inject
    public LicencesResourceImpl(LicensesService licensesService) {
        super(licensesService);
    }

    // Completely inhering the implementation of AbstractLicensesResourceImpl
}
