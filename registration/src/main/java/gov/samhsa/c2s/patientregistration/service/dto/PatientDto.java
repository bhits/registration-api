package gov.samhsa.c2s.patientregistration.service.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class PatientDto {
    private Long id;

    @NotEmpty
    @Size(min = 2, max = 30)
    private String lastName;

    @NotEmpty
    @Size(min = 2, max = 30)
    private String firstName;

    @NotEmpty
    @Pattern(regexp = "^[\\w-]+(\\.[\\w-]+)*@([a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*?\\.[a-zA-Z]{2,6}|(\\d{1,3}\\.){3}\\d{1,3})(:\\d{4})?$")
    private String email;

    @Past
    private LocalDate birthDate;

    @NotEmpty
    private String genderCode;

    private String socialSecurityNumber;

    private String telephone;
    private String address;
    private String city;
    private String stateCode;
    private String zip;

    private String resourceIdentifier;
    private String medicalRecordNumber;
    private String enterpriseIdentifier;
}