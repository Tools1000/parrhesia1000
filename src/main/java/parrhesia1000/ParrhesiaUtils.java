package parrhesia1000;

import parrhesia1000.nostr.event.Event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class ParrhesiaUtils {

    public static LocalDateTime getCreatedAt(Event event){
        return parse(event.getData().getCreatedAt());
    }

    public static LocalDateTime parse(long timeStampInSeconds){
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timeStampInSeconds),
                TimeZone.getDefault().toZoneId());
    }
}
