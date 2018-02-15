package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.ProjectTimeEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;
import java.util.List;

public class TimeEntryService {
    public static final Log log = LogFactory.getLog(ProjectService.class);
    public static TrackingDAOImpl dao;

    public TimeEntryService() {
        dao = new TrackingDAOImpl();
    }

    public ProjectTimeEntry createTimeEntry(int projectId, int employeeId, String station) throws Exception {
        log.info("Start of createTimeEntry with id " + projectId);
        ProjectTimeEntry projectTimeEntry = new ProjectTimeEntry();
        projectTimeEntry.setProjectId(projectId);
        projectTimeEntry.setEmployeeId(employeeId);
        projectTimeEntry.setStation(station);
        projectTimeEntry.setCreated(new Date());
        projectTimeEntry = dao.createTimeEntry(projectTimeEntry);
        log.info("End of createTimeEntry with id " + projectId);
        return projectTimeEntry;
    }

    public List<ProjectTimeEntry> getTimeEntries(int projectId) throws Exception{
        log.info("Start of getTimeEntries with id " + projectId);
        List<ProjectTimeEntry> timeEntries = null;
        if (dao.getProject(projectId) != null) {
            timeEntries = dao.getTimeEntries(projectId);
        }
        log.info("End of getTimeEntries with id " + projectId);
        return timeEntries;
    }
}
