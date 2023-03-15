package parrhesia1000;


import fx1000.Alerts;
import fx1000.GeneralExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriTemplate;

import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class ParrhesiaClient {

    private WebSocketSession session;

    public void connect(TextWebSocketHandler handler) {
        UriTemplate uriTemplate = new UriTemplate("wss://relay.damus.io");
        WebSocketClient client = new StandardWebSocketClient();
        CompletableFuture<WebSocketSession> websession = client.execute(handler, uriTemplate.toString());
        websession.whenComplete((webSocketSession, throwable) -> {
            if (throwable != null) {
                log.error("Client connect failed", throwable);
               Alerts.exceptionAlert("Client connect failed", throwable);
            } else {
                log.debug("Client connect complete, session: {}", webSocketSession);
                this.session = webSocketSession;
            }
        });
    }

    // Getter / Setter //

    public WebSocketSession getSession() {
        return session;
    }
}
