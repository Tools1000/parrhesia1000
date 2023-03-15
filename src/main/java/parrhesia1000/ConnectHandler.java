package parrhesia1000;

import org.springframework.web.socket.WebSocketSession;

public interface ConnectHandler {

    void connected(WebSocketSession session);

    void disconnected(WebSocketSession session);
}
