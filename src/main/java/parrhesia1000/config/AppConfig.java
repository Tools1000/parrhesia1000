package parrhesia1000.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "app")
@Configuration
public class AppConfig {

    private List<String> relays;

    private boolean debug;

    private String pub;

    private FeedConfig feed;

    private Cache cache;

}
