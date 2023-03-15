package parrhesia1000.nostr.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.config.AppConfig;
import parrhesia1000.nostr.event.Event;

@Slf4j
public abstract class EventHandlerPrototype implements EventHandler {

    protected final ObjectMapper objectMapper;

    protected final AppConfig appConfig;

    public EventHandlerPrototype(ObjectMapper objectMapper, AppConfig appConfig) {
        this.objectMapper = objectMapper;
        this.appConfig = appConfig;
    }

    @Override
    public void handleEvent(WebSocketSession session, Event event) {

        if (appConfig.isDebug()) {
            try {
                Thread.sleep(appConfig.getFeed().getDelay()); // delay feed somewhat
            } catch (InterruptedException e) {
                log.debug("Global feed delay was interrupted");
            }
        }

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
