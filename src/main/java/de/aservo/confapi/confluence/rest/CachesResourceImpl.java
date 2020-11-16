package de.aservo.confapi.confluence.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.confluence.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.confluence.model.CacheBean;
import de.aservo.confapi.confluence.rest.api.CachesResource;
import de.aservo.confapi.confluence.service.api.CachesService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(ConfAPI.CACHES)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Component
public class CachesResourceImpl implements CachesResource {

    private final CachesService cachesService;

    @Inject
    public CachesResourceImpl(
            final CachesService cachesService) {
        this.cachesService = cachesService;
    }

    @Override
    public Response getCaches() {
        return Response.ok(cachesService.getAllCaches()).build();
    }

    @Override
    public Response getCache(String name) {
        return Response.ok(cachesService.getCache(name)).build();
    }

    @Override
    public Response flushCache(String name) {
        cachesService.flushCache(name);
        return Response.ok(cachesService.getCache(name)).build();
    }

    @Override
    public Response updateCache(String name, CacheBean cache) {

        cachesService.setMaxCacheSize(name, cache.getMaxObjectCount());
        return Response.ok(cachesService.getCache(name)).build();
    }

}
