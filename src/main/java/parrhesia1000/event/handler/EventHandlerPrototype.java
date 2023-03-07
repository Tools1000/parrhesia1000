package parrhesia1000.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.event.Event;

@Slf4j
public abstract class EventHandlerPrototype implements EventHandler {

    protected final ObjectMapper objectMapper;

    public EventHandlerPrototype(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleEvent(WebSocketSession session, Event event) {

        if(event.getEvent().equals("EOSE")){
            return;
        }
        if(acceptedEvent(event.getSubscriptionId())) {
            try {
                doHandleEvent(session, event);
            } catch (Exception e) {
                log.error("Failed to handle event {}, reason: {}", event, e);
            }
        }
    }

    protected abstract void doHandleEvent(WebSocketSession session, Event event) throws Exception;

    protected abstract boolean acceptedEvent(String subscriptionId);
}
