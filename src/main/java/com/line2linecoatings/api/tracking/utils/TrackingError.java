package com.line2linecoatings.api.tracking.utils;

import javax.ws.rs.core.Response;
import java.util.List;

public class TrackingError
{
    private Response.Status status;
    private List<String> errorMessages;

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<String> getErrorMessages() {
        return  errorMessages;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
