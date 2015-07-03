package com.acumatica.vmware.service;

import javax.ws.rs.WebApplicationException;

public class VmWareException extends WebApplicationException  {
    private static final long serialVersionUID = 1L;
    
    public VmWareException() {
        this("Unknown exception");
    }
    
    public VmWareException(String message) {
        super(message);
    }
}
