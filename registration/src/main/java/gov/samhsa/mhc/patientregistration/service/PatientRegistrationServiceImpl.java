package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.infrastructure.PhrService;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientRegistrationServiceImpl implements PatientRegistrationService {

    @Autowired
    private HiePatientService hiePatientService;

    @Autowired
    private PhrService phrService;

    @Override
    public SignupDto addPatient(SignupDto signupDto) {
        //create patient in PHR
        signupDto = createPatientInPhr(signupDto);

        //Create patient in HIE
        signupDto = createPatientInHie(signupDto);

        //Update Patient Identifiers in PHR
        updatePatientInPhr(signupDto);

        return signupDto;
    }

    private SignupDto createPatientInPhr(SignupDto signupDto) {
        return phrService.createPatient(signupDto);
    }

    private SignupDto createPatientInHie(SignupDto signupDto) {
        try {
            signupDto = hiePatientService.addPatient(signupDto);
        } catch (FHIRFormatError fhirFormatError) {
            fhirFormatError.printStackTrace();
        }
        return signupDto;
    }

    private void updatePatientInPhr(SignupDto signupDto) {
        phrService.updatePatient(signupDto.getId(), signupDto);
    }
}


