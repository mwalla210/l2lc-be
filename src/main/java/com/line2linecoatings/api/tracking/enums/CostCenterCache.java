package com.line2linecoatings.api.tracking.enums;

import com.line2linecoatings.api.dao.TrackingDAOImpl;
import com.line2linecoatings.api.tracking.models.CostCenter;

import java.util.List;

/**
 * Created by eriksuman on 2/9/18.
 */
public class CostCenterCache {

    private static List<CostCenter> costCenters;
    private static boolean isInitialized = false;

    public static void initialize() throws Exception {
        costCenters = (new TrackingDAOImpl()).getCostCentersEnum();
        isInitialized = true;
    }

    public static boolean validateCostCenter(String name) throws Exception {
        if (!isInitialized)
            initialize();

        for (CostCenter c : costCenters)
            if (c.getName().equals(name))
                return true;

        return false;
    }

    public static String getCostCenterName(Integer id) throws Exception {
        if (!isInitialized)
            initialize();

        for (CostCenter c : costCenters)
            if (c.getId() == id)
                return c.getName();

        return null;
    }

    public static Integer getCostCenterId(String name) throws Exception{
        if (!isInitialized)
            initialize();

        for (CostCenter c : costCenters)
            if (c.getName().equals(name))
                return c.getId();

        return null;
    }


}
