package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.cloudfoundry.identity.uaa.rest.SearchResults;
import org.cloudfoundry.identity.uaa.scim.ScimGroup;
import org.cloudfoundry.identity.uaa.scim.ScimGroupMember;
import org.cloudfoundry.identity.uaa.scim.ScimUser;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientRegistrationServiceImpl implements PatientRegistrationService {

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @Value("${oauth.server.uaa}")
    private String uaaBaseUri;

    @Value("${oauth.resources.phr}")
    private String phrBaseUri;

    @Autowired
    HiePatientService hiePatientService;

    @Override
    public SignupDto addPatient(SignupDto signupDto) {

        //create user account in UAA
        createUserInUAA(signupDto);

        //create patient in PHR
        signupDto = createPatientInPhr(signupDto);

        //Create patient in HIE
        signupDto = createPatientInHie(signupDto);

        //Update Patient Identifiers in PHR
        updatePatientInPhr(signupDto);

        return signupDto;
    }

    private SignupDto createPatientInPhr(SignupDto signupDto) {
        signupDto = restTemplate.postForObject(getPhrPatientHealthUri(), signupDto, SignupDto.class);
        return signupDto;
    }

    private void createUserInUAA(SignupDto signupDto) {
        ScimUser scimUser = mapSignupdtoToScimuser(signupDto);
        ScimUser user = restTemplate.postForObject(uaaBaseUri + "/Users", scimUser, ScimUser.class);
        assignScopes(user.getId());
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
        //update MRN value in PHR
        restTemplate.put(getPhrPatientHealthUri() + "/" + signupDto.getId(), signupDto);
    }


    public ScimUser mapSignupdtoToScimuser(SignupDto signupDto) {

        //use email as username
        signupDto.setUsername(signupDto.getEmail());
        ScimUser scimUser = new ScimUser(null, signupDto.getUsername(), signupDto.getFirstName(), signupDto.getLastName());

        ScimUser.Email email = new ScimUser.Email();
        email.setValue(signupDto.getEmail());
        scimUser.setEmails(Collections.singletonList(email));
        scimUser.setPassword(signupDto.getPassword());

        ScimUser.PhoneNumber phone = new ScimUser.PhoneNumber();
        phone.setValue(signupDto.getTelephone());
        //TODO: Need to remove once activation workflow in place
        scimUser.setVerified(true);
        scimUser.setPhoneNumbers(Collections.singletonList(phone));

        return scimUser;
    }

    @Override
    public void assignScopes(String memberId) {
        ScimGroupMember scimGroupMember = new ScimGroupMember(memberId);
        List<ScimGroup> scimGroups = (List<ScimGroup>) getPatientScopes().getResources();

        for (ScimGroup group : scimGroups) {
            //Add the member to the groups.
            restTemplate.postForObject(uaaBaseUri + "/Groups/{groupId}/members", scimGroupMember, ScimGroupMember.class, group.getId());
        }
    }

    @Override
    public SearchResults<ScimGroup> getPatientScopes() {
        String queryParam = "displayName sw \"pcm\"  or displayName sw \"phr\"";
        Map<String, String> params = new HashMap<>();
        params.put("filter", queryParam);
        ResponseEntity<SearchResults<ScimGroup>> scimGroupResponse =
                restTemplate.exchange(uaaBaseUri + "/Groups?filter={filter}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<SearchResults<ScimGroup>>() {
                        },
                        params);
        return scimGroupResponse.getBody();
    }


    private final String getPhrPatientHealthUri() {
        return phrBaseUri + "/patients/";
    }
}


