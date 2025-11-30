package parrhesia1000.nostr.event.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.AuthorCache;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.request.RequestFactory;

/**
 * Listens for Author-metadata events and fills {@link AuthorCache} with new instances of {@link Author}.
 */
@Slf4j
public class AuthorsMetadataEventHandler extends EventHandlerPrototype {

    private final AuthorCache authorCache;

    public AuthorsMetadataEventHandler(ObjectMapper objectMapper, AppConfig appConfig, AuthorCache authorCache) {
        super(objectMapper, appConfig);
        this.authorCache = authorCache;
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) throws Exception {
        try {
            Author author = objectMapper.readValue(event.getData().getContent(), Author.class);
            author.setPubkey(event.getData().getPubkey());
            log.debug("Got new author metadata: {}", author);
            authorCache.put(author);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return RequestFactory.GET_AUTHORS_METADATA.equals(subscriptionId);
    }
}
