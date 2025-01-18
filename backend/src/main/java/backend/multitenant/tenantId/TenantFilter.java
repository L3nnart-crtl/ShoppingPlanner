package backend.multitenant.tenantId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class TenantFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String tenantId = httpRequest.getHeader("X-Tenant-ID");

        if (tenantId == null || tenantId.isEmpty()) {
            logger.info("No tenant ID in header, checking session...");
            tenantId = (String) httpRequest.getSession().getAttribute("tenantId");
        }

        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
            logger.info("Tenant ID set: " + tenantId);
        } else {
            logger.error("No tenant ID found in session or header!");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
