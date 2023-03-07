package parrhesia1000.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.FeedElementCache;
import parrhesia1000.event.Event;
import parrhesia1000.request.RequestFactory;

@Slf4j
@Component
public class PersonalFeedHandler extends EventHandlerPrototype {

    private final FeedElementCache feedElementCache;


    public PersonalFeedHandler(ObjectMapper objectMapper, FeedElementCache feedElementCache) {
        super(objectMapper);
        this.feedElementCache = feedElementCache;
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) {
        feedElementCache.add(event);
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return RequestFactory.SUBSCRIBED_AUTHORS_FEED.equals(subscriptionId);
    }
}
