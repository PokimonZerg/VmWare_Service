package com.acumatica.vmware.service;

import com.acumatica.vmware.service.model.Error;
import com.acumatica.vmware.service.model.SnapshotInfo;
import com.acumatica.vmware.service.ssh.*;
import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/service")
public class VmWareService {
    
    @EJB
    private SshClient sshClient;
    
    private Map<String, Integer> getVmList() throws JSchException, IOException {
        Map<String, Integer> map = new HashMap<>();
        
        sshClient.connect();
        String result = sshClient.execute("vim-cmd vmsvc/getallvms");
        sshClient.disconnect();
        
        String[] lines = result.split("\\n");
        for (int i = 1; i < lines.length; i++) {
            if ("".equals(lines[i])) continue;
            String[] parts = lines[i].split("\\s+");
            map.put(parts[1], Integer.parseInt(parts[0]));
        }
        
        return map;
    }
    
    private List<SnapshotInfo> getSnapshotList(int id) throws JSchException, IOException, ParseException {
        List<SnapshotInfo> snapshots = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy H:mm:ss");
        
        sshClient.connect();
        String result = sshClient.execute("vim-cmd vmsvc/snapshot.get " + id);
        sshClient.disconnect();
        
        String[] data = result.split("\\n");
        for (int i = 0; i < data.length; i++) {
            if (data[i].contains("Snapshot Name")) {
                SnapshotInfo snapshot = new SnapshotInfo();
                snapshot.setName(data[i].substring(data[i].indexOf(':') + 1).trim());
                snapshot.setId(Integer.parseInt(data[i + 1].substring(data[i + 1].indexOf(':') + 1).trim()));
                snapshot.setDescription(data[i + 2].substring(data[i + 2].indexOf(':') + 1).trim());
                snapshot.setCreateOn(dateFormat.parse(data[i + 3].substring(data[i + 3].indexOf(':') + 1).trim()));
                snapshot.setState(data[i + 4].substring(data[i + 4].indexOf(':') + 1).trim());
                snapshots.add(snapshot);
                i += 5;
            }
        }

        return snapshots;
    }
    
    @GET
    @Path("/snapshots/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<SnapshotInfo> snapshots(@PathParam("id") int id) throws JSchException, IOException, ParseException {
        return getSnapshotList(id);
    }
    
    @GET
    @Path("/restore/{name}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void restore(@PathParam("{name}") String name) throws VmWareException {
        
        try {
            Map<String, Integer> vms = getVmList();
        
            sshClient.connect();
            String result = sshClient.execute("vim-cmd vmsvc/getallvms");
            sshClient.disconnect();
        
        } 
        catch(JSchException | IOException e) {
            throw new VmWareException("Unexpected error: " + e.getMessage());
        }
    }
}
