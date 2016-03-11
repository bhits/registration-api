package gov.samhsa.mhc.patientregistration.service;

import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.valueset.AdministrativeGenderCodesEnum;
import ca.uhn.fhir.model.dstu.valueset.ContactSystemEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import gov.samhsa.mhc.patientregistration.config.FhirIdentifierProperties;
import gov.samhsa.mhc.patientregistration.service.dto.PatientDto;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class HieUserServiceImpl implements HieUserService {

    @Autowired
    MrnService mrnService;

    @Autowired
    FhirIdentifierProperties fhirIdentifierProperties;

    @Autowired
    IGenericClient fhirClient;

    @Autowired
    FhirValidator fhirValidator;

    @Autowired
    IParser fhirJsonParser;

    @Override
    public PatientDto addPatient(PatientDto patientDto) throws FHIRFormatError{

        Patient patient = patientDtoToPatient.apply(patientDto);

        //validate the resource
        ValidationResult validationResult = fhirValidator.validateWithResult(patient);
        System.out.println("Success: " + validationResult.isSuccessful());
        //throw format error if the validation is not successful
        if (! validationResult.isSuccessful()) {
            throw new FHIRFormatError("Patient Validation is not successful" + validationResult.getMessages());
        }

            /*
            Use the client to store a new patient resource instance
            Invoke the server create method (and send pretty-printed JSON
            encoding to the server
            instead of the default which is non-pretty printed XML)
            */
            MethodOutcome outcome = fhirClient.create().resource(patient).prettyPrint().execute();

            //print the output
            System.out.println("Patient Resource Id" + outcome.getId().getValue());
            return patientDto;
    }


    Function<PatientDto, Patient> patientDtoToPatient = new Function<PatientDto, Patient>() {
        @Override
        public Patient apply(PatientDto patientDto) {
            Patient patient = new Patient();
            //setting mandatory fields
            patient.addName().addFamily(patientDto.getLastName()).addGiven(patientDto.getFirstName());
            patient.addTelecom().setValue(patientDto.getTelephone()).setSystem(ContactSystemEnum.PHONE);
            patient.addTelecom().setValue(patientDto.getEmail()).setSystem(ContactSystemEnum.EMAIL);
            patient.setBirthDate(new DateTimeDt(patientDto.getBirthDate().toString()));
            patient.setGender(getPatientGender.apply(patientDto.getGenderCode()));
            patient.setActive(true);
            patient.setActive(false);

            //Add an Identifier
            setIdentifiers(patient, patientDto);

            //optional fields
            patient.addAddress().addLine(patientDto.getAddress()).setCity(patientDto.getCity()).setState(patientDto.getState()).setZip(patientDto.getZip());

            return patient;
        }
    };

    Function<String, AdministrativeGenderCodesEnum> getPatientGender = new Function<String, AdministrativeGenderCodesEnum>() {
        @Override
        public AdministrativeGenderCodesEnum apply(String codeString) {
            if (codeString != null && !"".equals(codeString) || codeString != null && !"".equals(codeString)) {
                if ("male".equals(codeString)) {
                    return AdministrativeGenderCodesEnum.M;
                } else if ("female".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderCodesEnum.F;
                } else if ("other".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderCodesEnum.UN;
                } else if ("unknown".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderCodesEnum.UNK;
                } else {
                    throw new IllegalArgumentException("Unknown AdministrativeGender code \'" + codeString + "\'");
                }
            } else {
                return null;
            }
        }
    };

    private void setIdentifiers(Patient patient, PatientDto patientDto) {

        //setting patient mrn
        String mrnValue = mrnService.generateMrn();
        patient.addIdentifier().setSystem(fhirIdentifierProperties.getMrnDomainId())
                .setValue(mrnValue);

        //set to patientDto
        patientDto.setMrnValue(mrnValue);


        // setting ssn value
        patient.addIdentifier().setSystem(fhirIdentifierProperties.getSsnSystem())
                .setValue(patientDto.getSsn()).setLabel(fhirIdentifierProperties.getSsnLabel());


    }


}
