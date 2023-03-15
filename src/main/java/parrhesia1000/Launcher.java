package parrhesia1000;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j

@SpringBootApplication
public class Launcher {

    public static void main(String[] args) {
        ParrhesiaApplication.main(args);
    }
}
