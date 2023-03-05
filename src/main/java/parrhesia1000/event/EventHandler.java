package parrhesia1000.event;

import org.springframework.web.socket.WebSocketSession;

public interface EventHandler {

    void handleEvent(WebSocketSession session, Event event);
}
