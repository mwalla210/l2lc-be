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

@Path("costcenter")
public class CostCenterResource extends BasicResource{
    public static final Log log = LogFactory.getLog(CostCenterResource.class);
    public static CostCenterService costCenterService;

    public CostCenterResource() {
        costCenterService = new CostCenterService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCostCenters(@Context HttpHeaders headers) throws Exception {
        List<CostCenter> costCenters;

        try {
            costCenters = costCenterService.getAllCostCenters();
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        return getResponse(Response.Status.OK, costCenters);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCostCenterById(@PathParam("id") int id, @Context HttpHeaders headers) throws Exception{
        CostCenter costCenter = null;

        try {
            costCenter = costCenterService.getCostCenter(id);
        } catch (Exception ex) {
            log.error(headers);
            throw ex;
        }

        if (costCenter == null) {
            return getResponse(Response.Status.NOT_FOUND);
        }

        return getResponse(Response.Status.OK, costCenter);
    }


}
