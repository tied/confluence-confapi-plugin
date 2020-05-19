package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.rest.AbstractLicensesResourceImpl;
import de.aservo.atlassian.confapi.service.api.LicensesService;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
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
