package parrhesia1000;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import parrhesia1000.dto.Author;
import parrhesia1000.nostr.event.Event;

import java.time.LocalDateTime;

@Slf4j
@Getter
public class FeedContentElement {

    private final Event event;

    private final ObjectProperty<Author> author;

    private final AuthorCache authorCache;

    private final AuthorCache.Listener listener = new AuthorCache.Listener() {
        @Override
        public void newAuthor(Author author) {
            Platform.runLater(()->{
                if(getEvent().getData().getPubkey().equals(author.getPubkey())){
                    setAuthor(author);
                    authorCache.getListenerList().remove(listener);
                }
            });
        }
    };

    public  FeedContentElement(Event event, Author author, AuthorCache authorCache) {
        this.event = event;
        this.author = new SimpleObjectProperty<>(author);
        this.authorCache = authorCache;
        authorCache.getListenerList().add(listener);
    }

    public LocalDateTime getCreatedAt() {
        return ParrhesiaUtils.getCreatedAt(event);
    }

    public String getText() {
        return event.getData().getContent();
    }

    // FX Getter / Setter //


    public Author getAuthor() {
        return author.get();
    }

    public ObjectProperty<Author> authorProperty() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author.set(author);
    }
}
