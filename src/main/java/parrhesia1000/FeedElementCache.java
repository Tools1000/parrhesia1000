package parrhesia1000;

import jakarta.annotation.PreDestroy;
import javafx.application.HostServices;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import parrhesia1000.dto.Author;
import parrhesia1000.event.Event;
import parrhesia1000.ui.FeedContentBox;
import parrhesia1000.ui.PersonalFeed;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class FeedElementCache {

    private final Executor executor;

    private final ApplicationContext applicationContext;

    private final AppConfig appConfig;

    private final PersonalFeed personalFeed;

    private final Map<String, List<Event>> eventMap;

    private final Map<String, Author> authorsMap;

    private final ExecutorService executorService;

    public FeedElementCache(Executor executor, ApplicationContext applicationContext, AppConfig appConfig, PersonalFeed personalFeed) {
        this.executor = executor;
        this.applicationContext = applicationContext;
        this.appConfig = appConfig;
        this.personalFeed = personalFeed;
        this.eventMap = new LinkedHashMap<>();
        this.authorsMap = new LinkedHashMap<>();
        executorService = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    void shutdown(){
        executorService.shutdown();
    }

    public void add(Event event) {
        executor.execute(() -> doAdd(event));
    }

    private synchronized void doAdd(Event event) {
        List<Event> events = eventMap.get(event.getData().getPubkey());
        if (events == null) {
            events = new ArrayList<>();
        }
        log.debug("Event list for {} now {}", event.getData().getPubkey(), events.size());
        events.add(event);
        eventMap.put(event.getData().getPubkey(), events);
        findMatches();
        log.debug("Event cache now {}", eventMap.size());
    }

    public void add(Author author) {
        executor.execute(() -> doAdd(author));
    }

    private synchronized void doAdd(Author author) {
        authorsMap.put(author.getPubkey(), author);
        findMatches();
        log.debug("Authors cache now {}", authorsMap.size());
    }

    private void findMatches() {
        List<String> pubKeysToRemove = new ArrayList<>();
        for (Map.Entry<String, List<Event>> e : eventMap.entrySet()) {
            Author author = authorsMap.get(e.getKey());
            if (author != null) {
                for (Event event : e.getValue()) {
                    addContent(new FeedContentElement(event, author));
                }
                pubKeysToRemove.add(e.getKey());
            }
        }

        for (String pubKey : pubKeysToRemove) {
            eventMap.remove(pubKey);
        }
    }

    private void addContent(FeedContentElement feedContent) {
        executorService.execute(() -> {
            try {
                Thread.sleep(appConfig.getFeed().getDelay());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            doAddContent(feedContent);
        });
    }

    private void doAddContent(FeedContentElement feedContent) {
        log.debug("Adding Feed element");
        Platform.runLater(() -> {
            personalFeed.addElement(new FeedContentBox(executor, applicationContext.getBean(HostServices.class), feedContent));
        });
    }
}
