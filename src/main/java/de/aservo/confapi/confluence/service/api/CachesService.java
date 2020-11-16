package de.aservo.confapi.confluence.service.api;

import de.aservo.confapi.confluence.model.CacheBean;
import de.aservo.confapi.confluence.model.CachesBean;

public interface CachesService {

    CachesBean getAllCaches();

    CacheBean getCache(String name);

    void setMaxCacheSize(String name, int newValue);

    void flushCache(String name);

}
