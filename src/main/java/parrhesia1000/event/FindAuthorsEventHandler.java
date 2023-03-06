package parrhesia1000.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.Filters;
import parrhesia1000.request.Request;

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
        log.debug("Found authors: {}", authors);
        subscribeToAuthors(session, authors);
    }

    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return "find-authors".equals(subscriptionId);
    }

    private List<String> getAuthorsFromEvent(Event event) {
        List<String> authors = new ArrayList<>();
        if ("find-authors".equals(event.getSubscriptionId())) {
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
            Request req = buildSubscribeAuthorsRequest(authors);
            String json = objectMapper.writeValueAsString(req);
            log.debug("Sending {}", json);
            session.sendMessage(new TextMessage(json));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request buildSubscribeAuthorsRequest(List<String> authors) {
        Filters filters = new Filters();
        filters.getKinds().add(1);
        filters.getAuthors().addAll(authors);
        Request req = new Request();
        req.setSubscriptionId("subscribed-authors-feed");
        req.setFilters(filters);
        return req;
    }
}
