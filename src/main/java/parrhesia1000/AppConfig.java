package parrhesia1000;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "app")
@Configuration
public class AppConfig {

    private boolean debug = true;

    private String pub;

    private Feed feed;
}
