package parrhesia1000.ui;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ui")
public class UiConfig {

    private String appTitle;

    private int initialWidth;

    private int initialHeight;

    private String overrideLocale;
}
