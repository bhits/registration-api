package gov.samhsa.c2s.patientregistration.service;

import gov.samhsa.c2s.patientregistration.config.IdentifierProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class MrnServiceImpl.
 */
@Service
public class MrnServiceImpl implements MrnService {

    @Autowired
    IdentifierProperties identifierProperties;

    @Override
    public String generateMrn() {
        //TODO: Make sure the randomly generating MRN does not exist in PHR database
        return generateRandomMrn();
    }

    /**
     * Generate random mrn.
     *
     * @return the string
     */
    private String generateRandomMrn() {
        StringBuilder localIdIdBuilder = new StringBuilder();
        if (null != identifierProperties.getMrnPrefix()) {
            localIdIdBuilder.append(identifierProperties.getMrnPrefix());
            localIdIdBuilder.append(".");
        }
        localIdIdBuilder.append(RandomStringUtils
                .randomAlphanumeric((identifierProperties.getMrnIdLength())));
        return localIdIdBuilder.toString().toUpperCase();
    }
}