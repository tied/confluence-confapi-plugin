package de.aservo.confapi.confluence.model.util;

import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import de.aservo.confapi.confluence.model.CacheBean;

import javax.validation.constraints.NotNull;

public class CacheBeanUtil {

    /**
     * Build CacheBean cache.
     *
     * @return the cache
     */
    @NotNull
    public static CacheBean toCacheBean(
            @NotNull final ManagedCache managedCache) {

        CacheBean cacheBean = new CacheBean();
        cacheBean.setName(managedCache.getName());
        cacheBean.setCurrentHeapSizeInByte(managedCache.getStatistics().get(CacheStatisticsKey.HEAP_SIZE).get());
        cacheBean.setEffectivenessInPercent(getEffectiveness(managedCache));
        cacheBean.setMaxObjectCount(managedCache.currentMaxEntries());
        cacheBean.setUtilisationInPercent(getUtilization(managedCache));
        cacheBean.setFlushable(managedCache.isFlushable());

        return cacheBean;
    }

    private static double getEffectiveness(ManagedCache cache) {
        long hit = cache.getStatistics().get(CacheStatisticsKey.HIT_COUNT).get();
        long miss = cache.getStatistics().get(CacheStatisticsKey.MISS_COUNT).get();
        return (double) hit * 100 / (hit + miss);
    }


    private static Double getUtilization(ManagedCache cache) {
        // currentMaxEntries can be null so check this first

        long objects = cache.getStatistics().get(CacheStatisticsKey.SIZE).get();
        Integer size = cache.currentMaxEntries();

        if (size != null) {
            return (double) objects * 100 / size;
        }
        return null;
    }

    private CacheBeanUtil() {
    }
}
