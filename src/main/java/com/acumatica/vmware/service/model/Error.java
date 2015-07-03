package com.acumatica.vmware.service.model;

import com.acumatica.vmware.service.VmWareException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.exception.ExceptionUtils;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlRootElement()
public class Error {
    private String message;
    private String trace;
    
    public Error() {
    }
    
    public Error (VmWareException exception) {
        this.message = exception.getMessage();
        this.trace = ExceptionUtils.getStackTrace(exception);
    }
            
    /*public Error(String message) {
        this.message = message;
        //this.message = e.getMessage();
        //this.trace = ExceptionUtils.getStackTrace(e);
    }*/

    @XmlElement
    public String getMessage() {
        return message;
    }

    /*public void setMessage(String message) {
        this.message = message;
    }*/

    @XmlElement
    public String getTrace() {
        return trace;
    }

    /*public void setTrace(String trace) {
        this.trace = trace;
    }*/
}
