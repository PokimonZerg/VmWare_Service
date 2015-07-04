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

    @XmlElement
    public String getMessage() {
        return message;
    }

    @XmlElement
    public String getTrace() {
        return trace;
    }
}
