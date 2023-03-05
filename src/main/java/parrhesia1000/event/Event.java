package parrhesia1000.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(shape=JsonFormat.Shape.ARRAY)
public class Event {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class EventData {

        @JsonProperty("content")
        private String content;

        @JsonProperty("created_at")
        private long createdAt;

        @JsonProperty("id")
        private String id;

        @JsonProperty("kind")
        private Integer kind;

        @JsonProperty("pubkey")
        private String pubkey;

        @JsonProperty("sig")
        private String sig;

        @JsonProperty("tags")
        private List<List<String>> tags;

    }

    private String event;
    private String subscriptionId;
    private EventData data;

}
