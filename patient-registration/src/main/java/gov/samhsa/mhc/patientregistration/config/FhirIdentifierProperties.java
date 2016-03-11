package gov.samhsa.mhc.patientregistration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="identifier")
public class FhirIdentifierProperties {

    private String mrnPrefix;
    private String mrnDomainId;
    private String mrnDomainType;
    private String mrnDomainLabel;
    private String mrnIdLength;
    private String mrnSystem;
    private String mrnUse;
    private String ssnSystem;
    private String ssnLabel;

    public String getMrnPrefix() {
        return mrnPrefix;
    }

    public void setMrnPrefix(String mrnPrefix) {
        this.mrnPrefix = mrnPrefix;
    }

    public String getMrnDomainId() {
        return mrnDomainId;
    }

    public void setMrnDomainId(String mrnDomainId) {
        this.mrnDomainId = mrnDomainId;
    }

    public String getMrnDomainType() {
        return mrnDomainType;
    }

    public void setMrnDomainType(String mrnDomainType) {
        this.mrnDomainType = mrnDomainType;
    }

    public String getMrnDomainLabel() {
        return mrnDomainLabel;
    }

    public void setMrnDomainLabel(String mrnDomainLabel) {
        this.mrnDomainLabel = mrnDomainLabel;
    }

    public String getMrnIdLength() {
        return mrnIdLength;
    }

    public void setMrnIdLength(String mrnIdLength) {
        this.mrnIdLength = mrnIdLength;
    }

    public String getMrnSystem() {
        return mrnSystem;
    }

    public void setMrnSystem(String mrnSystem) {
        this.mrnSystem = mrnSystem;
    }

    public String getMrnUse() {
        return mrnUse;
    }

    public void setMrnUse(String mrnUse) {
        this.mrnUse = mrnUse;
    }

    public String getSsnSystem() {
        return ssnSystem;
    }

    public void setSsnSystem(String ssnSystem) {
        this.ssnSystem = ssnSystem;
    }

    public String getSsnLabel() {
        return ssnLabel;
    }

    public void setSsnLabel(String ssnLabel) {
        this.ssnLabel = ssnLabel;
    }
}
