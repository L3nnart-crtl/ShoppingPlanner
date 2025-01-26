package backend.multitenant.authentication;

import backend.multitenant.tenantId.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * This filter is responsible for retrieving the tenant ID from the HTTP session and setting it in the
 * TenantContext. It ensures that each request is associated with the correct tenant. Additionally,
 * it logs the authentication status and tenant ID.
 */
@Component
public class TenantSessionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TenantSessionFilter.class);

    /**
     * This method is called for each HTTP request. It retrieves the tenant ID from the session
     * and sets it in the TenantContext. It also checks for the user's authentication status
     * and logs relevant information.
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain to pass the request and response to the next filter.
     * @throws ServletException If an exception occurs during the filter processing.
     * @throws IOException If an I/O error occurs during the filter processing.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);  // Retrieve existing session
        if (session != null) {
            String tenantId = (String) session.getAttribute("tenantId");
            if (tenantId != null) {
                TenantContext.setCurrentTenant(tenantId);
                logger.info("Tenant ID found in session: " + tenantId);

                // Check if the user is authenticated
                SecurityContext securityContext = SecurityContextHolder.getContext();
                Authentication authentication = securityContext.getAuthentication();

                if (authentication != null) {
                    logger.info("Authenticated user: " + authentication.getName());
                } else {
                    logger.error("Authentication is null for user!");
                }
            } else {
                logger.error("Tenant ID not found in session!");
            }
        } else {
            logger.error("Session is null!");
        }

        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }
}
