package gov.samhsa.mhc.patientregistration.service;

import gov.samhsa.mhc.patientregistration.config.FhirIdentifierProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class MrnServiceImpl.
 */
@Service
public class MrnServiceImpl implements  MrnService{

    @Autowired
    FhirIdentifierProperties fhirIdentifierProperties;
    @Override
    public String generateMrn() {
        //TODO: Need to cross verify with existing mrns from phr db
        return generateRandomMrn();
    }

    /**
     * Generate random mrn.
     *
     * @return the string
     */
    private String generateRandomMrn() {
        StringBuilder localIdIdBuilder = new StringBuilder();
        if (null != fhirIdentifierProperties.getMrnPrefix()) {
            localIdIdBuilder.append(new String(fhirIdentifierProperties.getMrnPrefix()));
            localIdIdBuilder.append(".");
        }
        localIdIdBuilder.append(RandomStringUtils
                .randomAlphanumeric((Integer.parseInt(fhirIdentifierProperties.getMrnIdLength()))));
        return localIdIdBuilder.toString().toUpperCase();
    }

}
