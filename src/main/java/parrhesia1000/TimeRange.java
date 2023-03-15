package parrhesia1000;

import lombok.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Setter
@Getter
@ToString
public class TimeRange {

    private LocalDateTime until, since;

    public TimeRange(Duration duration) {
        this(LocalDateTime.now(), LocalDateTime.now().minus(duration));
    }

    public TimeRange(LocalDateTime until, LocalDateTime since) {
        this.until = until;
        this.since = since;
    }

    public TimeRange updateUntil(){
        return new TimeRange(LocalDateTime.now(), until);
    }


    public TimeRange updateSince(Duration duration){
        return new TimeRange(since, since.minus(duration));
    }

    public TimeRange merge(TimeRange t1, TimeRange t2){
        LocalDateTime until = t1.getUntil().isAfter(t2.getUntil()) ? t1.getUntil() : t2.getUntil();
        LocalDateTime since = t1.getSince().isBefore(t2.getSince()) ? t1.getSince() : t2.getSince();
        return new TimeRange(until, since);
    }


}
