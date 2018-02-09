package com.line2linecoatings.api.resources;

import com.line2linecoatings.api.tracking.models.CostCenter;
import com.line2linecoatings.api.tracking.services.CostCenterService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

@Path("cost-center")
public class CostCenterResource extends BasicResource{
    public static final Log log = LogFactory.getLog(CostCenterResource.class);
    public static CostCenterService costCenterService;

    public CostCenterResource() {
        costCenterService = new CostCenterService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCostCenters(@Context HttpHeaders headers) throws Exception {
        List<String> costCenters;

        try {
            costCenters = costCenterService.getAllCostCenters();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, costCenters);
    }
}
