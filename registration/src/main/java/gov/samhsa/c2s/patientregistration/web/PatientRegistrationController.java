package gov.samhsa.c2s.patientregistration.web;

import gov.samhsa.c2s.patientregistration.service.PatientRegistrationService;
import gov.samhsa.c2s.patientregistration.service.dto.SignupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatientRegistrationController {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PatientRegistrationService patientRegistrationService;

    @RequestMapping(value = "/patients", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody SignupDto signupDto) {
        patientRegistrationService.addPatient(signupDto);
    }
}
