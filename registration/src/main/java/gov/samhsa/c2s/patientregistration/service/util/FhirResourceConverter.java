package gov.samhsa.c2s.patientregistration.service.util;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.dstu2.valueset.ContactPointSystemEnum;
import ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import gov.samhsa.c2s.patientregistration.service.dto.SignupDto;
import gov.samhsa.c2s.patientregistration.config.IdentifierProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

public class FhirResourceConverter {

    @Autowired
    IdentifierProperties identifierProperties;
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
    Function<SignupDto, Patient> signupDtoToPatient = new Function<SignupDto, Patient>() {
        @Override
        public Patient apply(SignupDto signupDto) {
            Patient patient = new Patient();
            //setting mandatory fields
            patient.addName().addFamily(signupDto.getLastName()).addGiven(signupDto.getFirstName());
            patient.addTelecom().setValue(signupDto.getEmail()).setSystem(ContactPointSystemEnum.EMAIL);
            patient.setBirthDate(new DateDt(signupDto.getBirthDate()));
            patient.setGender(getPatientGender.apply(signupDto.getGenderCode()));
            patient.setActive(true);

            //Add an Identifier
            setIdentifiers(patient, signupDto, signupDto.getMedicalRecordNumber());

            //optional fields
            patient.addAddress().addLine(signupDto.getAddress()).setCity(signupDto.getCity()).setState(signupDto.getStateCode()).setPostalCode(signupDto.getZip());
            if (null != signupDto.getTelephone() && !signupDto.getTelephone().isEmpty())
                patient.addTelecom().setValue(signupDto.getTelephone()).setSystem(ContactPointSystemEnum.PHONE);
            return patient;
        }
    };

    public Patient convertToPatient(SignupDto signupDto) {
        return signupDtoToPatient.apply(signupDto);
    }

    private void setIdentifiers(Patient patient, SignupDto signupDto, String medicalRecordNumber) {

        patient.addIdentifier().setSystem(identifierProperties.getMrnDomainLabel())
                .setUse(IdentifierUseEnum.OFFICIAL).setValue(medicalRecordNumber).setSystem(identifierProperties.getMrnDomainId());

        // setting ssn value
        if (signupDto.getSocialSecurityNumber() != null && signupDto.getSocialSecurityNumber().length() > 0)
            patient.addIdentifier().setSystem(identifierProperties.getSsnSystem())
                    .setValue(signupDto.getSocialSecurityNumber()).setSystem(identifierProperties.getSsnLabel());
    }
}