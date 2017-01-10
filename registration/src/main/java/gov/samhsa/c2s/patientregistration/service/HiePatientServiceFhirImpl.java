package gov.samhsa.c2s.patientregistration.service;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.c2s.common.log.Logger;
import gov.samhsa.c2s.common.log.LoggerFactory;
import gov.samhsa.c2s.patientregistration.config.FhirServiceConfig;
import gov.samhsa.c2s.patientregistration.service.dto.SignupDto;
import gov.samhsa.c2s.patientregistration.service.exception.FHIRFormatErrorException;
import gov.samhsa.c2s.patientregistration.service.util.FhirResourceConverter;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(FhirServiceConfig.class)
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


    @Override
    public SignupDto addPatient(SignupDto signupDto) {
        logger.info("FHIR is enabled, calling HIE for patient registration");
        // convert c2s patient object to FHIR Patient object
        Patient patient = fhirResourceConverter.convertToPatient(signupDto);

        //logs FHIRPatient into json and xml format in debug mode
        logFHIRPatient(patient);

        //validate the resource
        ValidationResult validationResult = fhirValidator.validateWithResult(patient);
        logger.debug("validationResult.isSuccessful(): " + validationResult.isSuccessful());

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

    private void logFHIRPatient(Patient fhirPatient) {
        logger.debug(() -> fhirContext.newXmlParser().setPrettyPrint(true)
                .encodeResourceToString(fhirPatient));
        logger.debug(() -> fhirContext.newJsonParser().setPrettyPrint(true)
                .encodeResourceToString(fhirPatient));
    }
}