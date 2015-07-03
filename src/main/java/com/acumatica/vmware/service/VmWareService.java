package com.acumatica.vmware.service;

import com.acumatica.vmware.service.model.Error;
import com.acumatica.vmware.service.ssh.*;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/service")
public class VmWareService {
    
    @EJB
    SshClient SshClient;
    
    @GET
    @Path("/restore/{name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Error restore(@PathParam("{name}") String name) throws Exception {
        //Error r = new Error();
        //return r;
        
        SshClient.connect(5);
        throw new VmWareException("da da da!");
        
        //return "test me";
    }
    
    @GET
    @Path("/test")
    //@Produces(MediaType.APPLICATION_JSON)
    public String test() throws Exception {
        //Error r = new Error();
        //r.setMessage("ttt");
        
        return "test me";
        
        //return "test me";
    }
}
