package com.line2linecoatings.api.tracking.services;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.CostCenter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Set;

public class CostCenterService {
    public static final Log log = LogFactory.getLog(CostCenterService.class);
    public TrackingDAOImpl dao;

    public CostCenterService() {
        this.dao = new TrackingDAOImpl();
    }

    public List<String> getAllCostCenters() throws Exception {
        log.info("Start of getAllCostCenters in service");
        List<String> costCenters = null;
        costCenters = dao.getCostCentersEnum();
        log.info("End of getAllCostCenters in service");
        return costCenters;
    }
}
