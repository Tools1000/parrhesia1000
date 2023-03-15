package parrhesia1000;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Slf4j
@Component
public class TimeRangeMap {

    private final Map<String, TimeRange> map = new LinkedHashMap<>();
}
