package parrhesia1000;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import parrhesia1000.event.NewAuthorEvent;
import tools1000.SimpleCache;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthorCache {

    public interface Listener {
        void newAuthor(Author author);
    }

    private final List<Listener> listenerList;

    private final AppConfig appConfig;
    private final SimpleCache<String, Author> cache;

    public AuthorCache(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.cache = new SimpleCache<>(appConfig.getCache().getAuthorCacheSize());
        this.listenerList = new ArrayList<>();
    }

    public synchronized Author getAuthor(String pub){
        return cache.getMap().get(pub);
    }

    public synchronized Author put(Author author){
        if(author.getPubkey() == null){
            throw new IllegalArgumentException("Pub key null");
        }
        for(Listener listener : listenerList){
            listener.newAuthor(author);
        }
        return cache.getMap().put(author.getPubkey(), author);
    }



    public int getSize() {
        return cache.getMap().size();
    }

    @Async
    @EventListener
    public void newAuthorEvent(NewAuthorEvent newAuthorEvent){
        put(newAuthorEvent.author());
    }

    // Getter / Setter //

    public synchronized List<Listener> getListenerList() {
        return listenerList;
    }
}
