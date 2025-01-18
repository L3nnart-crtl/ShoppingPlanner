package backend.multitenant.tenantId;

import backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Benutzer aus der Datenbank laden
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Rückgabe eines UserDetails-Objekts, das Spring Security benötigt
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())  // Passwort aus der User-Entität
                .roles("USER")  // Beispiel: Hier könnte man dynamisch Rollen hinzufügen
                .build();
    }
}
