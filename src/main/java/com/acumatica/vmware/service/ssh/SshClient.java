package com.acumatica.vmware.service.ssh;

import com.jcraft.jsch.JSch;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.inject.Default;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

@Stateful
public class SshClient {
    
    @Context
    private ServletContext context;
    
    private JSch jsch;
    
    //@Override
    public void connect(int i) {
        String username = context.getInitParameter("username");
    }
    
    @Remove
    public void remove() {
        
    }
}
