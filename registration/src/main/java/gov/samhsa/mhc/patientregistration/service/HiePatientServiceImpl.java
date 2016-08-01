package gov.samhsa.mhc.patientregistration.service;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import gov.samhsa.mhc.patientregistration.service.exception.FHIRFormatErrorException;
import gov.samhsa.mhc.patientregistration.service.util.FhirResourceConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class HiePatientServiceImpl implements HiePatientService {

    @Autowired
    private IGenericClient fhirClient;

    @Autowired
    private FhirValidator fhirValidator;

    @Autowired
    private IParser fhirJsonParser;

    @Autowired
    private FhirResourceConverter fhirResourceConverter;

    @Override
    public SignupDto addPatient(SignupDto signupDto) {

        Patient patient = fhirResourceConverter.convertToPatient(signupDto);

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

        //TODO : Need to store Eid value once integrate with IExhub
        //  signupDto.setResourceIdentifier(outcome.getId().getIdPart());

        //print the output
        // System.out.println("Patient Resource Id" + signupDto.getResourceIdentifier());

        return signupDto;
    }
}
