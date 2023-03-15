package parrhesia1000;

import parrhesia1000.nostr.event.Event;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class ParrhesiaUtils {

    public static LocalDateTime getCreatedAt(Event event){
        return getLocalDateTimeFromTimestamp(event.getData().getCreatedAt());
    }

    public static LocalDateTime getLocalDateTimeFromTimestamp(long timeStampInSeconds){
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timeStampInSeconds),
                TimeZone.getDefault().toZoneId());
    }

    public static long getSecondsTimestampFromLocalDateTime(LocalDateTime localDateTime){
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        return timestamp.getTime() / 1000;
    }
}
