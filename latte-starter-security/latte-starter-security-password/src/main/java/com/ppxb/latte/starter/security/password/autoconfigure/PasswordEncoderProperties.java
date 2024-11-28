package com.ppxb.latte.starter.security.password.autoconfigure;

import com.ppxb.latte.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(PropertiesConstants.SECURITY_PASSWORD)
public class PasswordEncoderProperties {

    private boolean enabled = true;

    private String encodingId = "bcrypt";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEncodingId() {
        return encodingId;
    }

    public void setEncodingId(String encodingId) {
        this.encodingId = encodingId;
    }
}
