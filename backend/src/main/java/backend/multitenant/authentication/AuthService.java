package backend.multitenant.authentication;

import backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import backend.multitenant.tenantId.User;

import java.util.UUID;

/**
 * Service class that handles user registration, authentication, and tenant ID management.
 * It provides methods to register a new user, authenticate an existing user, and manage tenant IDs.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor to initialize the AuthService with required repositories and password encoder.
     *
     * @param userRepository The repository used to interact with user data in the database.
     * @param passwordEncoder The password encoder used to encode and match passwords.
     */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the given username, password, and tenant ID.
     * If the tenant ID is not provided, a new tenant ID will be generated.
     * The password is securely encoded using BCrypt before saving the user.
     *
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @param tenantId The tenant ID for the new user (can be null or empty to generate a new one).
     * @throws IllegalArgumentException If the username already exists.
     */
    public void register(String username, String password, String tenantId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // If no tenantId is provided, generate a new one
        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = generateTenantId();
        }

        // Hash the password with BCrypt
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(username, encodedPassword, tenantId);
        userRepository.save(user);
    }

    /**
     * Generates a new tenant ID as a unique UUID string.
     *
     * @return A randomly generated UUID string to be used as a tenant ID.
     */
    private String generateTenantId() {
        return UUID.randomUUID().toString();  // Generate a random UUID as the tenant ID
    }

    /**
     * Authenticates a user by checking the provided username and password.
     * If authentication is successful, the tenant ID of the user is returned.
     *
     * @param username The username of the user trying to log in.
     * @param password The password of the user trying to log in.
     * @return The tenant ID of the authenticated user.
     * @throws IllegalArgumentException If the user is not found or the password is incorrect.
     */
    public String authenticateAndGetTenantId(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify the password using BCrypt
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user.getTenantId();
        } else {
            throw new IllegalArgumentException("Wrong password");
        }
    }
}
