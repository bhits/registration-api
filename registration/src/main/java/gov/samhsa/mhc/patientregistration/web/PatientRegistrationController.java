package gov.samhsa.mhc.patientregistration.web;

import gov.samhsa.mhc.patientregistration.service.PatientRegistrationService;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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
        try {
            patientRegistrationService.addPatient(signupDto);
        } catch (HttpClientErrorException e) {
            logger.error("    Stack Trace: " + e);
            logger.debug(e.getMessage(), e);
            throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("    Stack Trace: " + e);
            logger.debug(e.getMessage(), e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Service not available.");
        }
    }
}
