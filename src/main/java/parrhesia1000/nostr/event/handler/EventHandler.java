package parrhesia1000.nostr.event.handler;

import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.nostr.event.Event;

public interface EventHandler {

    void handleEvent(WebSocketSession session, Event event);
}
