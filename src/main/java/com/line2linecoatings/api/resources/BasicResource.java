package com.line2linecoatings.api.resources;

import javax.ws.rs.core.Response;

public class BasicResource
{
    public Response getResponse(Response.Status status, Object entity) {
        return Response.status(status).entity(entity).build();
    }

    public Response getResponse(Response.Status status) {
        return Response.status(status).build();
    }
}
