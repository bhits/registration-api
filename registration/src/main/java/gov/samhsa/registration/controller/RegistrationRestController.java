package gov.samhsa.registration.controller;

import gov.samhsa.registration.service.dto.SignupDto;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
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

    @Value("${registration.baseurl.uaa}")
    private String uaaBaseUrl;

    @Value("${registration.baseurl.phr}")
    private String phrBaseUrl;


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
          throw new HttpClientErrorException(e.getStatusCode(),e.getMessage());
       }catch(Exception e){
           throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Service not available.");
       }

    }

    public ScimUser mapSignupdtoToScimuser(SignupDto signupDto){

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
