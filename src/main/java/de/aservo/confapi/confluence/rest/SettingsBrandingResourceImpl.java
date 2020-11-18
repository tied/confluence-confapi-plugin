package de.aservo.confapi.confluence.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractSettingsBrandingResourceImpl;
import de.aservo.confapi.commons.service.api.SettingsBrandingService;
import de.aservo.confapi.confluence.filter.SysAdminOnlyResourceFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.SETTINGS + "/" + ConfAPI.BRANDING)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Component
public class SettingsBrandingResourceImpl extends AbstractSettingsBrandingResourceImpl {

    @Inject
    public SettingsBrandingResourceImpl(SettingsBrandingService brandingService) {
        super(brandingService);
    }
}
