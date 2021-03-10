package dev.ny.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@EnableDiscoveryClient
public class IdServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(IdServiceApp.class, args);
    }

    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
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
class IdController {
    private final AtomicLong counter = new AtomicLong();
    private String hostname;
    public IdController() {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostname = "FAILED";
        }
    }

    @GetMapping("/")
    public Id nextId() {
        final long id = counter.getAndIncrement();
        log.info("{} GET /, returning with id: {} ",hostname, id);
        return new Id(id, hostname);
    }
}
