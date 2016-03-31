package gov.samhsa.mhc.patientregistration.infrastructure;

import gov.samhsa.mhc.patientregistration.config.OAuth2FeignClientConfig;
import gov.samhsa.mhc.patientregistration.service.dto.SignupDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

// TODO: remove OAuth2FeignClientConfig configuration when PHR API is refactored to a service that can be called by this API without OAuth2 token
@FeignClient(name = "phr", configuration = OAuth2FeignClientConfig.class)
public interface PhrService {

    @RequestMapping(value = "/patients", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    SignupDto createPatient(@Valid @RequestBody SignupDto signupDto);

    @RequestMapping(value = "/patients/{patientId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    void updatePatient(@PathVariable("patientId") Long patientId, @Valid @RequestBody SignupDto signupDto);
}
