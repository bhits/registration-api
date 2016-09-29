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
        if (null != identifierProperties.getMrnPrefix()) {
            localIdIdBuilder.append(new String(identifierProperties.getMrnPrefix()));
            localIdIdBuilder.append(".");
        }
        localIdIdBuilder.append(RandomStringUtils
                .randomAlphanumeric((Integer.parseInt(identifierProperties.getMrnIdLength()))));
        return localIdIdBuilder.toString().toUpperCase();
    }
}