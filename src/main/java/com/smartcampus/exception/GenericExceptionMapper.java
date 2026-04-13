package com.smartcampus.exception;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        ex.printStackTrace();
        return Response.status(500)
                .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                .type("application/json")
                .build();
    }
}