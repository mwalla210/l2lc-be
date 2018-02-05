package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;


public class ProjectService {
    public static final Log log = LogFactory.getLog(ProjectService.class);
    TrackingDAOImpl dao;

    public ProjectService() {
        dao = new TrackingDAOImpl();
    }

    public Project createProject(Project project) throws Exception {
        log.info("Start of CreateProject in Service");
        project.setCreated(new Date());
        project.setProjectStatus("Recieved"); // might want to create java enum for this
        Project createdProject = dao.createProject(project);
        log.info("End of CreateProject in Service");
        return createdProject;
    }

    public Project getProject(int id) throws Exception {
        log.info("Start of getProject in Service with id " + id);
        Project project = dao.getProject(id);
        log.info("End of getProject in Service with id " + id);
        return project;
    }

}
