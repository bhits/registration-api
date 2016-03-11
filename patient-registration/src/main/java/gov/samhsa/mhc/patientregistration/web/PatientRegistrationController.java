package gov.samhsa.mhc.patientregistration.web;

import gov.samhsa.mhc.patientregistration.service.RegistrationService;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping("/users")
public class PatientRegistrationController
{

    @Autowired
    private RegistrationService registrationService;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


   @RequestMapping(value = "/signup", method = RequestMethod.POST)
   @PreAuthorize("#oauth2.hasScope('phr.hie_write','scim.write','registration.write','uaa.admin')")
   public void signup(@RequestBody SignupDto signupDto){
       try {
            registrationService.addPatient(signupDto);
       }catch(HttpClientErrorException e){
          logger.error("    Stack Trace: "+e);
          throw new HttpClientErrorException(e.getStatusCode(),e.getMessage());
       }catch(Exception e){
           logger.error("    Stack Trace: "+e);
           throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Service not available.");
       }
    }

    @RequestMapping(value = "/AssignPatientScopes/member/{memberId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#oauth2.hasScope('scim.write','registration.write','uaa.admin')")
    public void addUserToGroups(@PathVariable("memberId") String memberId){
        try {
            registrationService.assignScopes(memberId);
        }catch(HttpClientErrorException e){
            logger.error("    Stack Trace: "+e);
            throw new HttpClientErrorException(e.getStatusCode(),e.getMessage());
        }catch(Exception e){
            logger.error("    Stack Trace: "+e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Service not available.");
        }
    }



    @RequestMapping(value = "/PatientScopes", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#oauth2.hasScope('uaa.admin')")
    public SearchResults<ScimGroup> getMHCGroups(){
        try {

            return registrationService.getPatientScopes();

        }catch(HttpClientErrorException e){
            logger.error("    Stack Trace: "+e);
            throw new HttpClientErrorException(e.getStatusCode(),e.getMessage());
        }catch(Exception e){
            logger.error("    Stack Trace: "+e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Service not available.");
        }

    }



}
