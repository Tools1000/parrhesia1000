package parrhesia1000.event.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.dto.Request;
import parrhesia1000.event.Event;
import parrhesia1000.request.RequestFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FindAuthorsEventHandler extends EventHandlerPrototype {


    public FindAuthorsEventHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) {
        List<String> authors = getAuthorsFromEvent(event);
        log.debug("Found {} authors", authors.size());
        subscribeToAuthors(session, authors);
        requestAuthorsMetadata(session, authors);
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return RequestFactory.FIND_AUTHORS_SUBSCRIPTION_ID.equals(subscriptionId);
    }

    private List<String> getAuthorsFromEvent(Event event) {
        List<String> authors = new ArrayList<>();
        if (RequestFactory.FIND_AUTHORS_SUBSCRIPTION_ID.equals(event.getSubscriptionId())) {
            if (event.getData() == null) {
                log.warn("No event data for {}", event);
            } else {
                for (List<String> tag : event.getData().getTags()) {
                    if ("p".equals(tag.get(0))) {
                        authors.add(tag.get(1));
                    }
                }
            }
        }
        return authors;
    }

    private void subscribeToAuthors(WebSocketSession session, List<String> authors) {
        try {
            Request req = RequestFactory.buildSubscribeAuthorsRequest(authors);
            String json = objectMapper.writeValueAsString(req);
            log.info("Requesting feed from {} authors", authors.size());
            log.debug("Sending {}", json);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void requestAuthorsMetadata(WebSocketSession session, List<String> authors) {
        try {
            Request req = RequestFactory.buildGetAuthorMetadataRequest(authors);
            String json = objectMapper.writeValueAsString(req);
            log.info("Requesting metadata for {} authors", authors.size());
            log.debug("Sending {}", json);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


}
