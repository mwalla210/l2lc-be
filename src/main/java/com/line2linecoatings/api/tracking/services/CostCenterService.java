package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.CostCenter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class CostCenterService {
    public static final Log log = LogFactory.getLog(CostCenterService.class);
    public TrackingDAOImpl dao;

    public CostCenterService() {
        this.dao = new TrackingDAOImpl();
    }

    public List<CostCenter> getAllCostCenters() throws Exception {
        log.info("Start of getAllCostCenters in service");
        List<CostCenter> costCenters = null;
        costCenters = dao.getAllCostCenters();
        log.info("End of getAllCostCenters in service");
        return costCenters;
    }

    public CostCenter getCostCenter(int id) throws Exception {
        log.info("Start of getCostCenter in service with id " + id);
        CostCenter costCenter = null;
        costCenter = dao.getCostCenter(id);
        log.info("End of getCostCenter in service with id " + id);
        return costCenter;
    }
}
