package backend.multitenant.tenantId;

/**
 * A utility class that provides thread-local storage for the current tenant ID.
 * It allows different threads to store and retrieve the tenant ID without interfering with other threads.
 * This is especially useful in multi-tenant applications where each request (handled by a separate thread) may have a different tenant context.
 */
public class TenantContext {

    // Thread-local storage for the tenant ID
    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    /**
     * Retrieves the tenant ID for the current thread.
     *
     * @return The tenant ID for the current thread, or null if not set.
     */
    public static String getCurrentTenant() {
        return currentTenant.get();
    }

    /**
     * Sets the tenant ID for the current thread.
     *
     * @param tenantId The tenant ID to set for the current thread.
     */
    public static void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    /**
     * Clears the tenant ID for the current thread.
     * This should be called after the request is processed to avoid memory leaks.
     */
    public static void clear() {
        currentTenant.remove();
    }
}
