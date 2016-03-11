package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.service.dto.PatientDto;
import org.hl7.fhir.exceptions.FHIRFormatError;

/**
 * Created by sadhana.chandra on 3/1/2016.
 */
public interface HieUserService {

    PatientDto addPatient(PatientDto patientDto) throws FHIRFormatError;
}
