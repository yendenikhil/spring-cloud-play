package dev.ny.router;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@SpringBootApplication
@EnableDiscoveryClient
public class RouterServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(RouterServiceApp.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build())
                .build());
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Transaction {
    private long id;
    private String hostname;
    private String status;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class Id {
    private long id;
    private String hostname;
}

@RestController
@Log4j2
class RouterController {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CircuitBreakerFactory<Resilience4JConfigBuilder.Resilience4JCircuitBreakerConfiguration, Resilience4JConfigBuilder> factory;

    @GetMapping("/transaction")
    public Transaction nextTransaction() {
        log.info("GET /transaction");
        final CircuitBreaker cb = factory.create("transaction");
        final Supplier<Transaction> runner = () -> {
            final ResponseEntity<Id> en = restTemplate.getForEntity("http://id-service/", Id.class);
            final Id body = en.getBody();
            assert body != null;
            log.debug("id: {}", body.toString());
            return new Transaction(body.getId(), body.getHostname(), "ACCEPTED");
        };
        return cb.run(runner, e -> {
            log.error("id-service not successful.", e);
            return new Transaction(-1, "NA", "FAILED");
        });
    }
}
