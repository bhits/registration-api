package gov.samhsa.c2s.patientregistration.service;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.c2s.patientregistration.config.FhirServiceConfig;
import gov.samhsa.c2s.patientregistration.service.dto.SignupDto;
import gov.samhsa.c2s.patientregistration.service.exception.FHIRFormatErrorException;
import gov.samhsa.c2s.patientregistration.service.util.FhirResourceConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.dstu3.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@ConditionalOnBean(FhirServiceConfig.class)
@Slf4j
public class HiePatientServiceFhirImpl implements HiePatientService {
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FhirContext fhirContext;

    @Autowired
    private IGenericClient fhirClient;

    @Autowired
    private FhirValidator fhirValidator;

    @Autowired
    private FhirResourceConverter fhirResourceConverter;

    @Value("${logging.path}")
    private String logOutputPath;

    @Override
    public SignupDto addPatient(SignupDto signupDto) {
        log.info("FHIR is enabled, calling HIE for patient registration");
        Patient patient = fhirResourceConverter.convertToPatient(signupDto);

        if(logger.isDebugEnabled())
            createPatientToLogMessage(patient, "patient" + patient.getId());

        //validate the resource
        ValidationResult validationResult = fhirValidator.validateWithResult(patient);
        log.debug("validationResult.isSuccessful(): " + validationResult.isSuccessful());
        //throw format error if the validation is not successful
        if (!validationResult.isSuccessful()) {
            throw new FHIRFormatErrorException("Patient Validation is not successful" + validationResult.getMessages());
        }

        /*
        Use the client to store a new patient resource instance
        Invoke the server create method (and send pretty-printed JSON
        encoding to the server
        instead of the default which is non-pretty printed XML)
        */
        MethodOutcome outcome = fhirClient.create().resource(patient).execute();

        //TODO: Need to store Eid value once integrate with IExhub
        return signupDto;
    }

    private void createPatientToLogMessage(Patient fhirPatient, String fileName) {
        String xmlEncodedGranularConsent = fhirContext.newXmlParser().setPrettyPrint(true)
                .encodeResourceToString(fhirPatient);
        try {
            FileUtils.writeStringToFile(new File(logOutputPath + "/XML/" + fileName + ".xml"), xmlEncodedGranularConsent);
            String jsonEncodedGranularConsent = fhirContext.newJsonParser().setPrettyPrint(true)
                    .encodeResourceToString(fhirPatient);
            FileUtils.writeStringToFile(new File(logOutputPath + "/JSON/" + fileName + ".json"), jsonEncodedGranularConsent);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }
}