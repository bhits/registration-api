package gov.samhsa.mhc.patientregistration.service;

import org.springframework.stereotype.Service;

/**
 * The Interface MrnService.
 */
@Service
public interface MrnService {

    /**
     * Generate mrn.
     *
     * @return the string
     */
    String generateMrn();

}

