package com.line2linecoatings.api.tracking.caches;

import com.line2linecoatings.api.tracking.models.DBEnumeration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriksuman on 2/11/18.
 */
public abstract class Cache {
    protected List<DBEnumeration> cache;
    protected boolean isInitialized = false;
    private Log log = LogFactory.getLog(Cache.class);

    public static CostCenterCache costCenterCache = new CostCenterCache();
    public static StationCache stationCache = new StationCache();
    public static JobTypeCache jobTypeCache = new JobTypeCache();
    public static ProjectStatusCache projectStatusCache = new ProjectStatusCache();
    public static ProjectPriorityCache projectPriorityCache = new ProjectPriorityCache();

    public abstract void initialize() throws Exception;

    public boolean validate(String name) throws Exception {
        log.info("isInit: "+isInitialized);

        if (!isInitialized)
            initialize();

        for (DBEnumeration d : cache)
            if (name.equals(d.getName()))
                return true;

        return false;
    }

    public boolean validate(Integer id) throws Exception {
        if (!isInitialized) {
            initialize();
        }

        for (DBEnumeration d : cache)
            if (id.equals(d.getId()))
                return true;

        return false;
    }

    public String getNameForId(Integer id) throws Exception {
        if (!isInitialized)
            initialize();

        for(DBEnumeration d : cache)
            if (id.equals(d.getId()))
                return d.getName();

        return null;
    }

    public Integer getIdForName(String name) throws Exception {
        if (!isInitialized)
            initialize();

        for(DBEnumeration d : cache)
            if (name.equalsIgnoreCase(d.getName()))
                return d.getId();

        return null;
    }

    public List<String> getAllNames() throws Exception {
        if (!isInitialized) {
            initialize();
        }

        return cache.stream().map(d -> d.getName()).collect(Collectors.toList());
    }

    public List<Integer> getAllIds() throws Exception {
        if (!isInitialized) {
            initialize();
        }

        return cache.stream().map(d -> d.getId()).collect(Collectors.toList());
    }
}
