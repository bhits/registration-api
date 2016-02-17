package gov.samhsa.registration.controller;

import gov.samhsa.registration.service.dto.SignupDto;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;

@RestController
public class RegistrationRestController
{

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Value("${oauth.server.uaa}")
    private String uaaBaseUrl;

    @Value("${oauth.resources.phr}")
    private String phrBaseUrl;

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


   @RequestMapping(value = "/signup", method = RequestMethod.POST)
   @PreAuthorize("#oauth2.hasScope('phr.hie.writeDocument','scim.write','registration.write','zones.uaa.admin')")
       public void signup(@RequestBody SignupDto signupDto){

       try {
       //TODO : check patient in PHR
       // ResponseEntity<Boolean> checkDuplicate = restTemplate.postForEntity(phrBaseUrl + "checkDuplicate", signupDto, Boolean.class);

       //TODO : validate signuoDto

       //create user aacount in UAA
       ScimUser scimUser = mapSignupdtoToScimuser(signupDto);

       ScimUser response = restTemplate.postForObject(uaaBaseUrl +"/Users", scimUser, ScimUser.class);

       //create patient in PHR
       restTemplate.postForEntity(phrBaseUrl + "/patients", signupDto, null);

       }catch(HttpClientErrorException e){
          logger.error("    Stack Trace: "+e);
          throw new HttpClientErrorException(e.getStatusCode(),e.getMessage());
       }catch(Exception e){
           logger.error("    Stack Trace: "+e);
           throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Service not available.");
       }

    }

    public ScimUser mapSignupdtoToScimuser(SignupDto signupDto){

    //use email as username
    signupDto.setUsername(signupDto.getEmail());
    ScimUser scimUser = new ScimUser(null,signupDto.getUsername(),signupDto.getFirstName(),signupDto.getLastName());

    ScimUser.Email email = new ScimUser.Email();
    email.setValue(signupDto.getEmail());
    scimUser.setEmails(Collections.singletonList(email));
    scimUser.setPassword(signupDto.getPassword());

    ScimUser.PhoneNumber phone = new ScimUser.PhoneNumber();
    phone.setValue(signupDto.getTelephone());
    scimUser.setPhoneNumbers(Collections.singletonList(phone));

    //TODO : setup group from user not working
    //scimUser.setGroups(Arrays.asList(new ScimUser.Group(null, "phr.hie.writeDocument")));

    return scimUser;
    }
}
