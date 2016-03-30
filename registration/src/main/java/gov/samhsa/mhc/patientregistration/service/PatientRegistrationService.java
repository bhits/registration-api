package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;


public interface PatientRegistrationService {
    SignupDto addPatient(SignupDto signupDto);
}
