package parrhesia1000.nostr.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.AuthorCache;
import parrhesia1000.EventCache;
import parrhesia1000.config.AppConfig;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.request.RequestSender;

@Slf4j
public class PersonalFeedHandler extends GenericFeedHandler {

    public PersonalFeedHandler(ObjectMapper objectMapper, AppConfig appConfig, String subscriptionId, AuthorCache authorCache, RequestSender requestSender, EventCache eventCache) {
        super(objectMapper, appConfig, subscriptionId, authorCache, requestSender, eventCache);
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) throws Exception {
        super.doHandleEvent(session, event);
    }
}
