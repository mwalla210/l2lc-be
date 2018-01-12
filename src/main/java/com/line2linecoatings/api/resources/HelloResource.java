package com.line2linecoatings.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessage() {
        return "Hello World!";
    }

}
