package parrhesia1000.event.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.FeedElementCache;
import parrhesia1000.dto.Author;
import parrhesia1000.event.Event;
import parrhesia1000.request.RequestFactory;

@Slf4j
@Component
public class AuthorMetadataEventHandler extends EventHandlerPrototype {

    private final FeedElementCache feedElementCache;

    public AuthorMetadataEventHandler(ObjectMapper objectMapper, FeedElementCache feedElementCache) {
        super(objectMapper);
        this.feedElementCache = feedElementCache;
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) throws JsonProcessingException {
        Author author = objectMapper.readValue(event.getData().getContent(), Author.class);
        if(author.getPubkey() != null){
            log.warn("Overriding pub key for author {}", author);
        }
        if(event.getData().getPubkey() == null){
            log.warn("Got no pub key from event");
        }
        author.setPubkey(event.getData().getPubkey());
        feedElementCache.add(author);
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return RequestFactory.GET_AUTHORS_METADATA.equals(subscriptionId);
    }
}
