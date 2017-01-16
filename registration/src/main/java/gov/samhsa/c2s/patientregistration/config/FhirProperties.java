package gov.samhsa.c2s.patientregistration.config;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hie-connection.fhir")
@Data
public class FhirProperties {

    @NotEmpty
    private String serverUrl;

    @NotEmpty
    private String fhirClientSocketTimeoutInMs;
}