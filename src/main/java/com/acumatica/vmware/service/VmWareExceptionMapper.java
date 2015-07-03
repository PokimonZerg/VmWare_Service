package com.acumatica.vmware.service;

import com.acumatica.vmware.service.model.Error;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class VmWareExceptionMapper implements ExceptionMapper<VmWareException> {

    @Context
    private HttpHeaders headers;
    
    @Override
    public Response toResponse(VmWareException exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Error(exception)).type(getAcceptType()).build();
    }
    
    private String getAcceptType(){
        List<MediaType> accepts = headers.getAcceptableMediaTypes();
        if (accepts!=null && accepts.size() > 0 && accepts.contains(MediaType.APPLICATION_JSON_TYPE)) {
            return MediaType.APPLICATION_JSON;
        } else {
            return MediaType.APPLICATION_XML;
        }
    }
}