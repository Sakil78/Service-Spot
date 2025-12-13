package Team.C.Service.Spot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/**
 * Configuration for REST client beans.
 * Provides RestTemplate for external API calls (geocoding, etc.)
 *
 * @author Team C - ServiceSpot
 * @version 1.0
 * @since 2025-11-30
 */
@Configuration
public class RestClientConfig {

    /**
     * Create RestTemplate bean for making HTTP requests to external APIs.
     * Configured with timeouts to prevent hanging requests.
     *
     * @return Configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);  // 10 seconds
        factory.setReadTimeout(10000);      // 10 seconds
        return new RestTemplate(factory);
    }
}

