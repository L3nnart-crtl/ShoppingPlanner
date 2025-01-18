package backend.multitenant.authentication;

import backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import backend.multitenant.tenantId.User;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String password, String tenantId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Wenn keine tenantId angegeben wird, generiere eine neue
        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = generateTenantId();
        }

        // Hash das Passwort mit BCrypt
        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(username, encodedPassword, tenantId);
        userRepository.save(user);
    }

    // Generiert eine UUID für die Tenant-ID
    private String generateTenantId() {
        return UUID.randomUUID().toString();  // Generiere eine zufällige UUID als Tenant-ID
    }

    public String authenticateAndGetTenantId(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Überprüfe das Passwort mit BCrypt
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user.getTenantId();
        } else {
            throw new IllegalArgumentException("Wrong password");
        }
    }
}
