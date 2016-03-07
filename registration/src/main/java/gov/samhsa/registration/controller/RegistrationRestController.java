package gov.samhsa.registration.controller;

import gov.samhsa.registration.service.dto.SignupDto;
import org.cloudfoundry.identity.uaa.authentication.Origin;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimGroup;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.cloudfoundry.identity.uaa.user.UaaAuthority;
import org.hsqldb.rights.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   @PreAuthorize("#oauth2.hasScope('phr.hie_write','scim.write','registration.write','uaa.admin')")
   public void signup(@RequestBody SignupDto signupDto){
       try {
       //TODO : check patient in PHR
       // ResponseEntity<Boolean> checkDuplicate = restTemplate.postForEntity(phrBaseUrl + "checkDuplicate", signupDto, Boolean.class);

       //TODO : validate signuoDto

       //create user aacount in UAA
       ScimUser scimUser = mapSignupdtoToScimuser(signupDto);
       ScimUser user = restTemplate.postForObject(uaaBaseUrl +"/Users",scimUser,ScimUser.class );
       AssignScopes(user.getId());
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

    @RequestMapping(value = "/AssignPatientScopes/member/{memberId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#oauth2.hasScope('scim.write','registration.write','uaa.admin')")
    public void addUserToGroups(@PathVariable("memberId") String memberId){
        try {
            AssignScopes(memberId);
        }catch(HttpClientErrorException e){
            logger.error("    Stack Trace: "+e);
            throw new HttpClientErrorException(e.getStatusCode(),e.getMessage());
        }catch(Exception e){
            logger.error("    Stack Trace: "+e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Service not available.");
        }
    }

    private void AssignScopes(String memberId) {
        ScimGroupMember scimGroupMember = new ScimGroupMember(memberId);
        List<ScimGroup> scimGroups = (List<ScimGroup>) getPatientScopes().getResources();

        for (ScimGroup group : scimGroups)
        {
            //Add the member to the groups.
            restTemplate.postForObject(uaaBaseUrl +"/Groups/{groupId}/members",scimGroupMember,ScimGroupMember.class,group.getId() );
        }
    }

    @RequestMapping(value = "/PatientScopes", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#oauth2.hasScope('uaa.admin')")
    public SearchResults<ScimGroup> getMHCGroups(){
        try {

            return getPatientScopes();

        }catch(HttpClientErrorException e){
            logger.error("    Stack Trace: "+e);
            throw new HttpClientErrorException(e.getStatusCode(),e.getMessage());
        }catch(Exception e){
            logger.error("    Stack Trace: "+e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"Service not available.");
        }

    }

    private SearchResults<ScimGroup> getPatientScopes()
    {
        String queryParam ="displayName sw \"pcm\"  or displayName sw \"phr\"";
        Map<String, String> params = new HashMap<>();
        params.put("filter", queryParam);
        ResponseEntity<SearchResults<ScimGroup>> scimGroupResponse =
                restTemplate.exchange(uaaBaseUrl +"/Groups?filter={filter}",
                HttpMethod.GET, null, new ParameterizedTypeReference<SearchResults<ScimGroup>>() {},
                params);
        return scimGroupResponse.getBody();
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

    return scimUser;
    }
}
