package parrhesia1000.nostr.event.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.AuthorCache;
import parrhesia1000.EventCache;
import parrhesia1000.FeedContentElement;
import parrhesia1000.GlobalEventCache;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import parrhesia1000.event.NewAuthorEvent;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.request.RequestFactory;
import parrhesia1000.request.RequestSender;
import parrhesia1000.ui.Feed;
import parrhesia1000.ui.control.FeedContentBox;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class GlobalFeedHandler extends EventHandlerPrototype {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final Feed feed;

    private final EventCache eventCache;

    private final AuthorCache authorCache;

    private final ExecutorService executorService;

    private final RequestSender requestSender;


    public GlobalFeedHandler(ObjectMapper objectMapper, AppConfig appConfig, ApplicationEventPublisher applicationEventPublisher, EventCache eventCache, AuthorCache authorCache, Feed feed, RequestSender requestSender) {
        super(objectMapper, appConfig);
        this.applicationEventPublisher = applicationEventPublisher;
        this.eventCache = eventCache;
        this.authorCache = authorCache;
        this.feed = feed;
        this.requestSender = requestSender;
        executorService = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    void shutdown() {
        executorService.shutdownNow();
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) {

        if (event.getSubscriptionId().equals(RequestFactory.GLOBAL_FEED)) {
            handleFeedEvent(session, event);
        }
    }

    private void handleAuthorEvent(WebSocketSession session, Event event) {
        try {
            Author author = objectMapper.readValue(event.getData().getContent(), Author.class);
            author.setPubkey(event.getData().getPubkey());
            applicationEventPublisher.publishEvent(new NewAuthorEvent(authorCache, author));
            updateElementsInFeed(author);
            updateElementsInCache(author);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateElementsInCache(Author author) {
        for (FeedContentElement event2 : new ArrayList<>(eventCache.getElements())) {
            if (event2.getEvent().getData().getPubkey().equals(author.getPubkey())) {
                Platform.runLater(() -> {
                    event2.authorProperty().set(author);
                });
            }
        }
    }

    private void updateElementsInFeed(Author author) {
        for (FeedContentBox event2 : new ArrayList<>(feed.getFeedContent())) {
            if (event2.getFeedContentElement().getEvent().getData().getPubkey().equals(author.getPubkey())) {
                Platform.runLater(() -> {
                    event2.getFeedContentElement().authorProperty().set(author);
                });
            }
        }
    }

    private void handleFeedEvent(WebSocketSession session, Event event) {
        Author author = authorCache.getAuthor(event.getData().getPubkey());
        if (author == null) {
            requestSender.sendRequest(session, RequestFactory.buildGetAuthorMetadataRequest(event.getData().getPubkey()));
        }
        Platform.runLater(() -> {
            eventCache.getElements().add(new FeedContentElement(event, author, authorCache));
        });
    }


    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return RequestFactory.GLOBAL_FEED.equals(subscriptionId) || RequestFactory.GET_AUTHOR_METADATA.equals(subscriptionId);
    }
}
