package gov.samhsa.mhc.patientregistration.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import gov.samhsa.mhc.patientregistration.service.util.FhirResourceConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sadhana.chandra on 3/4/2016.
 */
@Configuration
public class FhirServiceConfig {

    @Value("${fhir.serverUrl}")
    String fhirServerUrl;

    // Create a context
    FhirContext fhirContext = new FhirContext();

    @Bean
    public IGenericClient fhirClient() {
        // Create a client
        IGenericClient fhirClient = fhirContext.newRestfulGenericClient(fhirServerUrl);
        return fhirClient;
    }

    @Bean
    public IParser fhirXmlParser(){
        IParser fhirXmlParser = fhirContext.newXmlParser();
        return fhirXmlParser;
    }

    @Bean
    public IParser fhirJsonParser(){
        IParser fhirJsonParser = fhirContext.newJsonParser();
        return fhirJsonParser;
    }

    @Bean
    public FhirValidator fhirValidator(){
        FhirValidator fhirValidator = fhirContext.newValidator();
        return fhirValidator;
    }

    @Bean
    public FhirResourceConverter fhirResourceConverter(){
        FhirResourceConverter fhirResourceConverter = new FhirResourceConverter();
        return fhirResourceConverter;
    }
}
