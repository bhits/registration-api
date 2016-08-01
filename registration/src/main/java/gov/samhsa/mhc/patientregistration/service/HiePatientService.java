package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;

public interface HiePatientService {

    SignupDto addPatient(SignupDto signupDto);
}
