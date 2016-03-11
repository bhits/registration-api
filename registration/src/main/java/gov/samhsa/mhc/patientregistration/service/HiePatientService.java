package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.hl7.fhir.exceptions.FHIRFormatError;

/**
 * Created by sadhana.chandra on 3/1/2016.
 */
public interface HiePatientService {

    SignupDto addPatient(SignupDto signupDto) throws FHIRFormatError;
}
