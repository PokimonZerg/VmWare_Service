package com.acumatica.vmware.service;

import com.acumatica.vmware.service.model.SnapshotInfo;
import com.acumatica.vmware.service.model.VmInfo;
import com.acumatica.vmware.service.ssh.*;
import com.jcraft.jsch.JSchException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/service")
public class VmWareService {
    
    private Map<String, Integer> getVmList() throws JSchException, IOException {
        Map<String, Integer> map = new HashMap<>();
        
        SshClient sshClient = new SshClient();
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
        
        SshClient sshClient = new SshClient();
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
    
    private int getSnapshotByName(int vmId, String name) throws JSchException, IOException, ParseException {
        List<SnapshotInfo> snapshots = getSnapshotList(vmId);
        
        for (SnapshotInfo snapshot : snapshots) {
            if (snapshot.getName().equals(name)) {
                return snapshot.getId();
            }
        }
        
        throw new VmWareException("Snapshot with name '" + name + "' not found. Actual snapshot list: " + snapshots);
    }
    
    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<VmInfo> list() throws VmWareException {
        try {
            List<VmInfo> result = new ArrayList<>();
            Map<String, Integer> vms = getVmList();
            
            for (String s: vms.keySet()) {
                VmInfo info = new VmInfo();
                info.setName(s);
                info.setId(vms.get(s));
                result.add(info);
            }
            
            return result;
        }
        catch (JSchException | IOException e) {
            throw new VmWareException("Unexpected error: " + e.getMessage());
        }
    }
    
    @GET
    @Path("/snapshots")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<SnapshotInfo> snapshots(@QueryParam("vm") String vm) throws VmWareException {
        try {
            Map<String, Integer> vms = getVmList();
            if (vms.containsKey(vm) == false)
                throw new VmWareException("Virtual machine with name '" + vm + "' not found. Actual vm list: " + vms);
            int vmId = vms.get(vm);
            
            return getSnapshotList(vmId);
        }
        catch(JSchException | IOException | ParseException e) {
            throw new VmWareException("Unexpected error: " + e.getMessage());
        }
    }
    
    @GET
    @Path("/restore")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void restore(@QueryParam("vm") String vm, @QueryParam("snapshot") String snapshot) throws VmWareException {
        
        try {
            Map<String, Integer> vms = getVmList();
            if (vms.containsKey(vm) == false)
                throw new VmWareException("Virtual machine with name '" + vm + "' not found. Actual vm list: " + vms);
            int vmId = vms.get(vm);
            int snapshotId = getSnapshotByName(vmId, snapshot);
            
            SshClient sshClient = new SshClient();
            sshClient.connect();
            String result = sshClient.execute("vim-cmd vmsvc/snapshot.revert " + vmId + " " + snapshotId + " false");
            sshClient.disconnect();
        
        } 
        catch(JSchException | IOException | ParseException e) {
            throw new VmWareException("Unexpected error: " + e.getMessage());
        }
    }
}
