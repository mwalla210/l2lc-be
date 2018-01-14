package com.line2linecoatings.api.resources;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource
{
    public static final Log log = LogFactory.getLog(HelloResource.class);
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessage() {
        log.info("Hello Logging!");
        return "Hello World!";
    }

}
