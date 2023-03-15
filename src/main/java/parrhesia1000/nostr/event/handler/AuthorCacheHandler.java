package parrhesia1000.nostr.event.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.AuthorCache;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.request.RequestFactory;

import java.util.concurrent.Executor;

@Slf4j
public class AuthorCacheHandler extends EventHandlerPrototype {

    private final AuthorCache authorCache;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Executor executor;

    public AuthorCacheHandler(ObjectMapper objectMapper, AppConfig appConfig, AuthorCache authorCache, ApplicationEventPublisher applicationEventPublisher, Executor executor) {
        super(objectMapper, appConfig);
        this.authorCache = authorCache;
        this.applicationEventPublisher = applicationEventPublisher;
        this.executor = executor;
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) throws Exception {
        try {
            Author author = objectMapper.readValue(event.getData().getContent(), Author.class);
            author.setPubkey(event.getData().getPubkey());
            log.debug("Got new author metadata: {}", author);
            executor.execute(() -> authorCache.put(author));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return RequestFactory.GET_AUTHOR_METADATA.equals(subscriptionId);
    }
}
