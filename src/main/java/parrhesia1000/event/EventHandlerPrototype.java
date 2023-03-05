package parrhesia1000.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.WebSocketSession;

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
        if(acceptedEvent(event.getSubscriptionId())){
            doHandleEvent(session, event);
        }
    }

    protected abstract void doHandleEvent(WebSocketSession session, Event event);

    protected abstract boolean acceptedEvent(String subscriptionId);
}
