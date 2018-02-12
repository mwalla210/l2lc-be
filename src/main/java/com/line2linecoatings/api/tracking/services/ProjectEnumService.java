package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.caches.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class ProjectEnumService {
    public static final Log log = LogFactory.getLog(ProjectEnumService.class);
    public TrackingDAOImpl dao;

    public ProjectEnumService() {
        this.dao = new TrackingDAOImpl();
    }

    public List<String> getAllCostCenters() throws Exception {
        log.info("Start of getAllCostCenters in service");
        List<String> costCenters;
        costCenters = Cache.costCenterCache.getAllNames();
        log.info("End of getAllCostCenters in service");
        return costCenters;
    }

    public List<String> getAllJobTypes() throws Exception {
        log.info("Start of getAllJobTypes in service");
        List<String> jobTypes;
        jobTypes = Cache.jobTypeCache.getAllNames();
        log.info("End of getAllJobTypes in service");
        return jobTypes;
    }

    public List<String> getAllProjectStatuses() throws Exception {
        log.info("Start of getAllProjectStatuses in service");
        List<String> projectStatuses;
        projectStatuses = Cache.projectStatusCache.getAllNames();
        log.info("End of getAllProjectStatuses in service");
        return projectStatuses;
    }

    public List<String> getAllProjectPriorities() throws Exception {
        log.info("Start of getAllProjectPriorities in service");
        List<String> priorities;
        priorities = Cache.projectPriorityCache.getAllNames();
        log.info("End of getAllProjectPriorities in service");
        return priorities;
    }
}
