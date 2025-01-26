package backend.multitenant.tenantId;

import backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for loading user details based on the username. It interacts with
 * the user repository to retrieve user information and provides a Spring Security compatible
 * UserDetails object for authentication.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor to initialize the user repository.
     *
     * @param userRepository The repository used to fetch user data from the database.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details based on the provided username. This method retrieves the user from
     * the database and constructs a Spring Security UserDetails object.
     *
     * @param username The username of the user to load.
     * @return A UserDetails object containing the user's information.
     * @throws IllegalArgumentException if the user with the given username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        // Load the user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Return a UserDetails object that Spring Security can use
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())  // Password from the User entity
                .roles("USER")  // Example: Here we can dynamically add roles if needed
                .build();
    }
}
