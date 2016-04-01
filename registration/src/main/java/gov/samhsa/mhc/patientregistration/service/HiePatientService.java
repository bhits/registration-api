package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;

/**
 * Created by sadhana.chandra on 3/1/2016.
 */
public interface HiePatientService {

    SignupDto addPatient(SignupDto signupDto) throws Exception;
}
