package backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up a {@link WebClient} bean.
 * <p>
 * The {@link WebClient} is a non-blocking, reactive HTTP client used for making
 * HTTP requests to external services.
 * </p>
 */
@Configuration
public class WebClientConfig {

    /**
     * Creates and configures a {@link WebClient} bean.
     * <p>
     * This method configures the {@link WebClient} with:
     * <ul>
     *   <li>A maximum in-memory buffer size of 10 MB for handling large responses.</li>
     * </ul>
     * </p>
     *
     * @return a configured {@link WebClient} instance
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10 MB max
                .build();
    }
}
