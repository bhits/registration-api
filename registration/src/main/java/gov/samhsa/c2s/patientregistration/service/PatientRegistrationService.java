package gov.samhsa.c2s.patientregistration.service;

import gov.samhsa.c2s.patientregistration.service.dto.SignupDto;


public interface PatientRegistrationService {
    SignupDto addPatient(SignupDto signupDto);
}
