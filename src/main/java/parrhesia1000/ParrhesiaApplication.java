package parrhesia1000;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

@SpringBootApplication
public class ParrhesiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParrhesiaApplication.class, args);

    }

    private static URI buildUrl() {
        HexFormat hex = HexFormat.of();
        String hexString = hex.formatHex("npub1s5cmkc7lhcsku9qwmpg8kwr83a5y4en7psr9z247tgv72rcdk8psy7c27z".getBytes(StandardCharsets.UTF_8));
        UriTemplate uriTemplate = new UriTemplate("nostrconnect:// + " + hexString + "?relay={relay}&metadata={metadata}");
        Map<String, Object> vars = new HashMap<>();
        vars.put("relay", "wss://relay.damus.io");
        vars.put("metadata", new HashMap<String,String>());
        URI uri = uriTemplate.expand(vars);
        return uri;
    }
}
