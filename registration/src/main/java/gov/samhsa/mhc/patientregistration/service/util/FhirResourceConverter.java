package gov.samhsa.mhc.patientregistration.service.util;


import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import gov.samhsa.mhc.patientregistration.config.FhirIdentifierProperties;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;


public class FhirResourceConverter {

    @Autowired
    FhirIdentifierProperties fhirIdentifierProperties;

    public Patient convertToPatient(SignupDto signupDto) {
        Patient patient = signupDtoToPatient.apply(signupDto);

        return patient;
    }

    Function<SignupDto, Patient> signupDtoToPatient = new Function<SignupDto, Patient>() {
        @Override
        public Patient apply(SignupDto signupDto) {
            Patient patient = new Patient();
            //setting mandatory fields
            patient.addName().addFamily(signupDto.getLastName()).addGiven(signupDto.getFirstName());
            patient.addTelecom().setValue(signupDto.getTelephone()).setSystem(ContactPointSystemEnum.PHONE);
            patient.addTelecom().setValue(signupDto.getEmail()).setSystem(ContactPointSystemEnum.EMAIL);
            patient.setBirthDate(new DateDt(signupDto.getBirthDate()));
            patient.setGender(getPatientGender.apply(signupDto.getGenderCode()));
            patient.setActive(true);

            //Add an Identifier
            setIdentifiers(patient, signupDto, signupDto.getMedicalRecordNumber());

            //optional fields
            patient.addAddress().addLine(signupDto.getAddress()).setCity(signupDto.getCity()).setState(signupDto.getState()).setPostalCode(signupDto.getZip());

            return patient;
        }
    };


    Function<String, AdministrativeGenderEnum> getPatientGender = new Function<String, AdministrativeGenderEnum>() {
        @Override
        public AdministrativeGenderEnum apply(String codeString) {
            if (codeString != null && !"".equals(codeString) || codeString != null && !"".equals(codeString)) {
                if ("male".equalsIgnoreCase(codeString) || "M".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderEnum.MALE;
                } else if ("female".equalsIgnoreCase(codeString) || "F".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderEnum.FEMALE;
                } else if ("other".equalsIgnoreCase(codeString) || "O".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderEnum.OTHER;
                } else if ("unknown".equalsIgnoreCase(codeString) || "UN".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderEnum.UNKNOWN;
                } else {
                    throw new IllegalArgumentException("Unknown AdministrativeGender code \'" + codeString + "\'");
                }
            } else {
                return null;
            }
        }
    };

    private void setIdentifiers(Patient patient, SignupDto signupDto, String medicalRecordNumber) {

        //setting patient mrn
        // String mrnValue = mrnService.generateMrn();
       // patient.addIdentifier().setValue(medicalRecordNumber);
       // patient.setId(new IdDt(medicalRecordNumber));
        // setting MRN value

        patient.addIdentifier().setSystem(fhirIdentifierProperties.getMrnDomainLabel())
                    .setValue(medicalRecordNumber).setSystem(fhirIdentifierProperties.getMrnDomainId());

        // setting ssn value
        if(signupDto.getSocialSecurityNumber() != null && signupDto.getSocialSecurityNumber().length()>0)
            patient.addIdentifier().setSystem(fhirIdentifierProperties.getSsnSystem())
                    .setValue(signupDto.getSocialSecurityNumber()).setSystem(fhirIdentifierProperties.getSsnLabel());


    }
}
