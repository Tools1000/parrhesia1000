package parrhesia1000;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import tools1000.SimpleCache;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Component
public class AuthorCache {

    public interface Listener {
        void newAuthor(Author author);
    }

    private final List<Listener> listenerList;

    private final ObjectMapper objectMapper;

    private final Executor executor;

    private final AppConfig appConfig;
    private final SimpleCache<String, Author> cache;

    private final Set<String> requestingAuthors;

    public AuthorCache(ObjectMapper objectMapper, Executor executor, AppConfig appConfig) {
        this.objectMapper = objectMapper;
        this.executor = executor;
        this.appConfig = appConfig;
        this.cache = new SimpleCache<>(appConfig.getCache().getAuthorCacheSize());
        this.listenerList = new ArrayList<>();
        this.requestingAuthors = new LinkedHashSet<>();
    }

    synchronized Author getAuthor(String pub) {
        return cache.getMap().get(pub);
    }

    public synchronized Author getAuthor(String pub, WebSocketSession session) {
        Author author = getAuthor(pub);
        if (author != null) return author;
        if(!requestingAuthors.contains(pub)){
            requestingAuthors.add(pub);
            log.debug("Now requesting metadata for {} authors", requestingAuthors.size());
            new AuthorsMetadataRequester(objectMapper, session).requestAuthorsMetadata(pub);
        }
        return null;
    }

    public synchronized Author put(Author author) {
        if (author.getPubkey() == null) {
            throw new IllegalArgumentException("Pub key null");
        }
        Author result = cache.getMap().put(author.getPubkey(), author);
        requestingAuthors.remove(author.getPubkey());
        for (Listener listener : listenerList) {
            executor.execute(() -> {
                listener.newAuthor(author);
            });
        }
        log.debug("Still requesting metadata for {} authors", requestingAuthors.size());
        return result;
    }


    public int getSize() {
        return cache.getMap().size();
    }

    // Getter / Setter //

    public synchronized List<Listener> getListenerList() {
        return listenerList;
    }
}
