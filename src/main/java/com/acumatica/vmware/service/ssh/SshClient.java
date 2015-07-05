package com.acumatica.vmware.service.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.validation.constraints.NotNull;
import org.apache.commons.io.IOUtils;

public class SshClient {
    
    private final String username = "root";
    private final String password = "Golova2Slona";
    private final String address = "192.168.1.175";
    
    private JSch jsch;
    private Session session;
    
    public void connect() throws JSchException {
        jsch = new JSch();
        session = jsch.getSession(username, address, 22);
        
        session.setPassword(password);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        session.setConfig(config);
        session.connect();
    }
    
    public void disconnect() {
        if (session != null)
            session.disconnect();
        
        jsch = null;
    }
    
    public String execute(@NotNull String command) throws JSchException, IOException {
        if (session == null)
            return new String();
        
        ChannelExec channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        
        InputStream in = channel.getInputStream();
        channel.connect();
        
        StringBuilder builder = new StringBuilder();
        while(!channel.isClosed()){
            builder.append(IOUtils.toString(in));
        }
        
        if (in.available() > 1) builder.append(IOUtils.toString(in));
        
        channel.disconnect();
        
        return builder.toString();
    }
}
