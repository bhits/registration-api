package gov.samhsa.mhc.patientregistration.service.util;

import ca.uhn.fhir.model.dstu.resource.Patient;
import ca.uhn.fhir.model.dstu.valueset.AdministrativeGenderCodesEnum;
import ca.uhn.fhir.model.dstu.valueset.ContactSystemEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import gov.samhsa.mhc.patientregistration.config.FhirIdentifierProperties;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;


public class FhirResourceConverter {

    @Autowired
    FhirIdentifierProperties fhirIdentifierProperties;

    public Patient convertToPatient(SignupDto signupDto ){
         Patient patient = signupDtoToPatient.apply(signupDto);

        return patient;
    }

    Function<SignupDto, Patient> signupDtoToPatient = new Function<SignupDto, Patient>() {
        @Override
        public Patient apply(SignupDto signupDto) {
            Patient patient = new Patient();
            //setting mandatory fields
            patient.addName().addFamily(signupDto.getLastName()).addGiven(signupDto.getFirstName());
            patient.addTelecom().setValue(signupDto.getTelephone()).setSystem(ContactSystemEnum.PHONE);
            patient.addTelecom().setValue(signupDto.getEmail()).setSystem(ContactSystemEnum.EMAIL);
            patient.setBirthDate(new DateTimeDt(signupDto.getBirthDate()));
            patient.setGender(getPatientGender.apply(signupDto.getGenderCode()));
            patient.setActive(true);

            //Add an Identifier
            setIdentifiers(patient, signupDto, signupDto.getMedicalRecordNumber());

            //optional fields
            patient.addAddress().addLine(signupDto.getAddress()).setCity(signupDto.getCity()).setState(signupDto.getState()).setZip(signupDto.getZip());

            return patient;
        }
    };


    Function<String, AdministrativeGenderCodesEnum> getPatientGender = new Function<String, AdministrativeGenderCodesEnum>() {
        @Override
        public AdministrativeGenderCodesEnum apply(String codeString) {
            if (codeString != null && !"".equals(codeString) || codeString != null && !"".equals(codeString)) {
                if ("male".equalsIgnoreCase(codeString) || "M".equalsIgnoreCase(codeString) ) {
                    return AdministrativeGenderCodesEnum.M;
                } else if ("female".equalsIgnoreCase(codeString) || "F".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderCodesEnum.F;
                } else if ("other".equalsIgnoreCase(codeString) || "O".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderCodesEnum.UN;
                } else if ("unknown".equalsIgnoreCase(codeString) || "UN".equalsIgnoreCase(codeString)) {
                    return AdministrativeGenderCodesEnum.UNK;
                } else {
                    throw new IllegalArgumentException("Unknown AdministrativeGender code \'" + codeString + "\'");
                }
            } else {
                return null;
            }
        }
    };
    private void setIdentifiers(Patient patient, SignupDto signupDto,  String medicalRecordNumber) {

        //setting patient mrn
       // String mrnValue = mrnService.generateMrn();
        patient.addIdentifier().setSystem(fhirIdentifierProperties.getMrnDomainId())
                .setValue(medicalRecordNumber).setLabel(fhirIdentifierProperties.getMrnDomainLabel());

        // setting ssn value
        if(signupDto.getSsn() != null && signupDto.getSsn().length()>0)
            patient.addIdentifier().setSystem(fhirIdentifierProperties.getSsnSystem())
                    .setValue(signupDto.getSsn()).setLabel(fhirIdentifierProperties.getSsnLabel());


    }
}
