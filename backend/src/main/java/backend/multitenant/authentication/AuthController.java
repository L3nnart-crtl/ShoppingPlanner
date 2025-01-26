package backend.multitenant.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

/**
 * Controller that handles authentication-related requests such as registration, login, logout, and checking login status.
 * It manages user authentication, session handling, and tenant-specific information.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor to initialize the AuthController with required services.
     *
     * @param authService The service responsible for user authentication and registration.
     * @param authenticationManager The manager responsible for handling authentication requests.
     */
    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user.
     * This endpoint handles user registration and returns a response indicating the success or failure.
     *
     * @param registerRequest The request body containing user registration details like username, password, and tenantId.
     * @return A ResponseEntity indicating the result of the registration process.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        try {
            authService.register(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getTenantId());
            return ResponseEntity.ok("Registration successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Logs in a user.
     * This endpoint handles user login by authenticating the user's credentials and setting up their session.
     *
     * @param loginRequest The request body containing the user's login credentials.
     * @param session The HTTP session to store tenant-specific information and security context.
     * @return A ResponseEntity indicating the result of the login process.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            String tenantId = authService.authenticateAndGetTenantId(loginRequest.getUsername(), loginRequest.getPassword());
            System.out.println(tenantId);
            session.setAttribute("tenantId", tenantId);  // Set tenantId in session

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok("Login successful");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    /**
     * Logs out a user by invalidating their session.
     * This endpoint clears all session attributes and logs the user out.
     *
     * @param session The HTTP session to invalidate.
     * @return A ResponseEntity indicating the result of the logout process.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        if (session != null) {
            session.invalidate();  // Invalidate session on logout
        }
        return ResponseEntity.ok("Logout successful");
    }

    /**
     * Checks the login status of the user.
     * This endpoint verifies if the user is logged in by checking the session for a tenantId.
     *
     * @param session The HTTP session that holds user login information.
     * @return A ResponseEntity with status 200 if the user is logged in, or 401 if not.
     */
    @GetMapping("/status")
    public ResponseEntity<Void> checkLoginStatus(HttpSession session) {
        if (session != null && session.getAttribute("tenantId") != null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();  // Unauthorized if tenantId is not found
    }
}
