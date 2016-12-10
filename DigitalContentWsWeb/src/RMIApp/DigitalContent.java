package RMIApp;

import java.io.File;
import java.io.Serializable;

public class DigitalContent implements Serializable{
    private String description;
    private byte[] content;

    public DigitalContent(String description, byte[] content) {
        this.description = description;
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getContent() {
        return content;
    }
    
    
}
