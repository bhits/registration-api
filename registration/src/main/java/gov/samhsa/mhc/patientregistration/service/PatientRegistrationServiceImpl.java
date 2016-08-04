package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.common.log.Logger;
import gov.samhsa.mhc.common.log.LoggerFactory;
import gov.samhsa.mhc.patientregistration.infrastructure.PhrService;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class PatientRegistrationServiceImpl implements PatientRegistrationService {

    private final Logger logger = LoggerFactory.getLogger(this);

    @Value("${fhir.enabled}")
    private boolean fhirEnabled;

    @Autowired
    private HiePatientService hiePatientService;

    @Autowired
    private PhrService phrService;

    @Autowired
    private MrnService mrnService;

    @Override
    public SignupDto addPatient(SignupDto signupDto) {
        Assert.isNull(signupDto.getId(), "ID is not allowed to be provided for a new patient");
        final String mrn = mrnService.generateMrn();
        signupDto.setMedicalRecordNumber(mrn);
        if (fhirEnabled) {
            logger.debug(signupDto::toString);
            logger.info("FHIR is enabled, calling HIE for patient registration");
            //Create patient in HIE
            signupDto = createPatientInHie(signupDto);
            logger.debug(signupDto::toString);
        } else {
            logger.info("FHIR is disabled, skipping HIE for patient registration");
        }

        //create patient in PHR
        logger.info("Calling PHR to create patient");
        signupDto = createPatientInPhr(signupDto);
        logger.debug(signupDto::toString);

        return signupDto;
    }

    private SignupDto createPatientInPhr(SignupDto signupDto) {
        return phrService.createPatient(signupDto);
    }

    private SignupDto createPatientInHie(SignupDto signupDto) {
        try {
            signupDto = hiePatientService.addPatient(signupDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(e.getMessage(), e);
            throw e;
        }
        return signupDto;
    }

    private void updatePatientInPhr(SignupDto signupDto) {
        phrService.updatePatient(signupDto.getId(), signupDto);
    }
}


