package backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("backend")
public class ShoppingPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingPlannerApplication.class, args);
    }
}

