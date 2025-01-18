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

@Component
public class TenantSessionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TenantSessionFilter.class);

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
