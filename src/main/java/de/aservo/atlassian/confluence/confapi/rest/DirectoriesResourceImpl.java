package de.aservo.atlassian.confluence.confapi.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.rest.AbstractDirectoriesResourceImpl;
import de.aservo.atlassian.confapi.service.api.DirectoryService;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(ConfAPI.DIRECTORIES)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class DirectoriesResourceImpl extends AbstractDirectoriesResourceImpl {

    @Inject
    public DirectoriesResourceImpl(DirectoryService directoryService) {
        super(directoryService);
    }

    // Completely inhering the implementation of AbstractDirectoriesResourceImpl

}
