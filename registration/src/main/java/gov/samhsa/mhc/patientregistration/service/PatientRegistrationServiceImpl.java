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

    private final String getPhrPatientProfileUri(Long patientId) {
        return phrBaseUri + "/patients/" + patientId + "/profile";
    }

    private final String getPhrPatientHealthUri() {
        return phrBaseUri + "/patients/";
    }
}


