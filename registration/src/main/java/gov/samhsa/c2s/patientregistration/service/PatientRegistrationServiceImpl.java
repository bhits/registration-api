package gov.samhsa.c2s.patientregistration.service;

import gov.samhsa.c2s.patientregistration.infrastructure.PhrService;
import gov.samhsa.c2s.patientregistration.service.dto.SignupDto;
import gov.samhsa.mhc.common.log.Logger;
import gov.samhsa.mhc.common.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
public class PatientRegistrationServiceImpl implements PatientRegistrationService {

    private final Logger logger = LoggerFactory.getLogger(this);

    @Autowired
    private Optional<HiePatientService> hiePatientService;

    @Autowired
    private PhrService phrService;

    @Autowired
    private MrnService mrnService;

    @Override
    public SignupDto addPatient(SignupDto signupDto) {
        Assert.isNull(signupDto.getId(), "ID is not allowed to be provided for a new patient");
        final String mrn = mrnService.generateMrn();
        signupDto.setMedicalRecordNumber(mrn);
        logger.debug(signupDto::toString);

        //Create patient in HIE (if configured)
        signupDto = createPatientInHie(signupDto);
        logger.debug(signupDto::toString);

        //create patient in PHR
        signupDto = createPatientInPhr(signupDto);
        logger.debug(signupDto::toString);

        return signupDto;
    }

    private SignupDto createPatientInPhr(SignupDto signupDto) {
        logger.info("Calling PHR to create patient");
        return phrService.createPatient(signupDto);
    }

    private SignupDto createPatientInHie(SignupDto signupDto) {
        try {
            logger.info(() -> hiePatientService
                    .map(hie -> "HiePatientService is configured: " + hie.toString())
                    .orElse("No HiePatientService is available, skipping HIE for patient registration"));
            return hiePatientService.map(hie -> hie.addPatient(signupDto)).orElse(signupDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.debug(e.getMessage(), e);
            throw e;
        }
    }

    private void updatePatientInPhr(SignupDto signupDto) {
        phrService.updatePatient(signupDto.getId(), signupDto);
    }
}


