package parrhesia1000;

import lombok.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Setter
@Getter
@ToString
public class TimeRange {

    public static int DEFAULT_RANGE_IN_MINUTES = 5;

    private LocalDateTime start, end;

    private Duration iterationDuration;

    public TimeRange() {
        this(LocalDateTime.now(), LocalDateTime.now().minusMinutes(DEFAULT_RANGE_IN_MINUTES));
    }

    public TimeRange(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
        iterationDuration = Duration.of(DEFAULT_RANGE_IN_MINUTES, ChronoUnit.MINUTES);
    }

    /**
     * Returns a time range between old start and now
     * @return
     */
    public TimeRange updateStart(){
        return new TimeRange(LocalDateTime.now(), start);
    }

    /**
     * Returns a time range between old end and end
     * @return
     */
    public TimeRange updateEnd(){
        return updateEnd(iterationDuration);
    }

    public TimeRange updateEnd(Duration duration){
        return new TimeRange(end, end.minus(duration));
    }

    public TimeRange merge(TimeRange t1, TimeRange t2){
        LocalDateTime start = t1.getStart().isAfter(t2.getStart()) ? t1.getStart() : t2.getStart();
        LocalDateTime end = t2.getEnd().isBefore(t2.getEnd()) ? t1.getEnd() : t2.getEnd();
        return new TimeRange(start, end);
    }


}
