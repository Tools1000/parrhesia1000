package parrhesia1000.nostr.event;

import java.util.List;
import java.util.stream.Collectors;

public class EventParser {

    public static List<List<String>> getReplyingPubs(Event event) {
        return event.getData().getTags().stream().filter(t -> t.get(0).equals("p")).collect(Collectors.toList());
    }
}
