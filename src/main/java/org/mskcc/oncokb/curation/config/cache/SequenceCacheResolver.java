package org.mskcc.oncokb.curation.config.cache;

import java.util.ArrayList;
import java.util.Collection;
import org.mskcc.oncokb.curation.config.application.ApplicationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;

public class SequenceCacheResolver implements CacheResolver {

    private final ApplicationProperties applicationProperties;
    private final CacheManager cacheManager;
    private final CacheNameResolver cacheNameResolver;

    public SequenceCacheResolver(
        CacheManager cacheManager,
        ApplicationProperties applicationProperties,
        CacheNameResolver cacheNameResolver
    ) {
        this.cacheManager = cacheManager;
        this.applicationProperties = applicationProperties;
        this.cacheNameResolver = cacheNameResolver;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<Cache> caches = new ArrayList<>();

        if (context.getMethod().getName() == "findOneByTranscriptAndSequenceType") {
            caches.add(
                cacheManager.getCache(this.cacheNameResolver.getCacheName(CacheCategory.SEQUENCE, CacheKeys.SEQUENCE_BY_TRASCRIPT_AND_TYPE))
            );
        }

        return caches;
    }
}
