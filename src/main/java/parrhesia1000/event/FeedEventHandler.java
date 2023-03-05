package parrhesia1000.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
public class FeedEventHandler extends EventHandlerPrototype {

    public FeedEventHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) {
        log.debug("Received feed event: {}", event);
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return "find-authors".equals(subscriptionId);
    }
}
