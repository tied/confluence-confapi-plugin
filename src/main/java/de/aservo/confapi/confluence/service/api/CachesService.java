package de.aservo.confapi.confluence.service.api;

import de.aservo.confapi.confluence.model.CacheBean;

import java.util.Collection;

public interface CachesService {

    Collection<CacheBean> getAllCaches();

    CacheBean getCache(String name);

    void setMaxCacheSize(String name, int newValue);

    void flushCache(String name);

}
