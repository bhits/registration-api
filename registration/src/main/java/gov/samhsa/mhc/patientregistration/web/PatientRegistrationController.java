package gov.samhsa.mhc.patientregistration.web;

import gov.samhsa.mhc.patientregistration.service.PatientRegistrationService;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/users")
public class PatientRegistrationController {

    @Autowired
    private PatientRegistrationService patientRegistrationService;

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody SignupDto signupDto) {
        try {
            patientRegistrationService.addPatient(signupDto);
        } catch (HttpClientErrorException e) {
            logger.error("    Stack Trace: " + e);
            throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("    Stack Trace: " + e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Service not available.");
        }
    }
}
