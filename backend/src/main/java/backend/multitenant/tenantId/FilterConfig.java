package backend.multitenant.tenantId;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This configuration class registers the TenantFilter to be applied to all incoming requests.
 * It ensures that the TenantFilter is part of the Spring Boot application’s filter chain
 * and handles tenant-specific functionality such as setting the tenant context.
 */
@Configuration
public class FilterConfig {

    /**
     * Registers the TenantFilter as a filter in the Spring Boot application.
     * This filter will be applied to all incoming requests (indicated by "/*").
     *
     * @return A FilterRegistrationBean that configures the TenantFilter.
     */
    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilter() {
        // Create a new FilterRegistrationBean for the TenantFilter
        FilterRegistrationBean<TenantFilter> registrationBean = new FilterRegistrationBean<>();

        // Set the filter to be used
        registrationBean.setFilter(new TenantFilter());

        // Apply the filter to all URL patterns (/*)
        registrationBean.addUrlPatterns("/*");

        // Return the configured registration bean
        return registrationBean;
    }
}
