package gov.samhsa.mhc.patientregistration.config;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


@Configuration
public class SecurityConfig {

    private static final String RESOURCE_ID = "phr";

    @Bean
    public ResourceServerConfigurer resourceServer(SecurityProperties securityProperties) {
        return new ResourceServerConfigurerAdapter() {
            @Override
            public void configure(ResourceServerSecurityConfigurer resources) {
                resources.resourceId(RESOURCE_ID);
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {
                if (securityProperties.isRequireSsl()) {
                    http.requiresChannel().anyRequest().requiresSecure();
                }
                 http.authorizeRequests()
                        .antMatchers(HttpMethod.POST, "/users/signup*//**").access("#oauth2.hasScope('phr.hie_write','scim.write','registration.write','uaa.admin')")
                        .antMatchers(HttpMethod.POST, "/users/AssignPatientScopes/member/*//**").access("#\"#oauth2.hasScope('scim.write','registration.write','uaa.admin')")
                        .antMatchers(HttpMethod.GET, "/users/PatientScopes/*//**").access("#oauth2.hasScope('uaa.admin')")
                         .antMatchers(HttpMethod.OPTIONS, "/*//**").permitAll()
                         .anyRequest().denyAll();
            }

        };
    }

    // Uses SHA-256 with multiple iterations and a random salt value.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }
}
