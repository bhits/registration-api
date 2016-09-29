package gov.samhsa.c2s.patientregistration.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "identifier")
@Data
public class IdentifierProperties {

    private String mrnPrefix;
    private String mrnDomainId;
    private String mrnDomainType;
    private String mrnDomainLabel;
    private String mrnIdLength;
    private String mrnSystem;
    private String mrnUse;
    private String ssnSystem;
    private String ssnLabel;
}