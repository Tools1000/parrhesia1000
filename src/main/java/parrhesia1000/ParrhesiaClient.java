package parrhesia1000;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.util.UriTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class ParrhesiaClient {

    private final SessionCallbackHandler sessionCallbackHandler;

    public void connect() {
        UriTemplate uriTemplate = new UriTemplate("wss://relay.damus.io");
        WebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<WebSocketSession> websession = client.execute(sessionCallbackHandler, uriTemplate.toString());
        websession.whenComplete((webSocketSession, throwable) -> {
            if (throwable != null) {
                log.error("Client connect failed", throwable);
            } else {
                log.debug("Client connect complete, session: {}", webSocketSession);
            }
        });
    }
}
