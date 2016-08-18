package gov.samhsa.c2s.patientregistration.service;

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

