package parrhesia1000.event.handler;

import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.event.Event;

public interface EventHandler {

    void handleEvent(WebSocketSession session, Event event);
}
