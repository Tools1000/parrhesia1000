package parrhesia1000.nostr.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.AuthorCache;
import parrhesia1000.EventCache;
import parrhesia1000.FeedContentElement;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.request.RequestFactory;
import parrhesia1000.request.RequestSender;

import java.util.*;

@Slf4j
public class GenericFeedHandler extends EventHandlerPrototype {

    protected final String subscriptionId;

    protected final AuthorCache authorCache;

    protected final RequestSender requestSender;

    protected final EventCache eventCache;

    public GenericFeedHandler(ObjectMapper objectMapper, AppConfig appConfig, String subscriptionId, AuthorCache authorCache, RequestSender requestSender, EventCache eventCache) {
        super(objectMapper, appConfig);
        this.subscriptionId = subscriptionId;
        this.authorCache = authorCache;
        this.requestSender = requestSender;
        this.eventCache = eventCache;
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) throws Exception {
        Author author = authorCache.getAuthor(event.getData().getPubkey(), session);
        Platform.runLater(() -> {
            eventCache.getElements().add(new FeedContentElement(event, author, authorCache));
        });
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return this.subscriptionId.equals(subscriptionId);
    }
}
