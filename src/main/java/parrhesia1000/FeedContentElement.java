package parrhesia1000;

import lombok.Getter;
import parrhesia1000.event.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Getter
public class FeedContentElement {

    private final Event event;

    public FeedContentElement(Event event) {
        this.event = event;


    }

    public LocalDateTime getCreatedAt(){
        return
                LocalDateTime.ofInstant(Instant.ofEpochSecond(event.getData().getCreatedAt()),
                        TimeZone.getDefault().toZoneId());
    }

    public String getText(){
        return event.getData().getContent();
    }
}
