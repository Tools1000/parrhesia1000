package parrhesia1000.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import parrhesia1000.Filters;

@Data
@JsonFormat(shape= JsonFormat.Shape.ARRAY)
public class Request {

    @JsonProperty
    String req = "REQ";

    @JsonProperty
    String subscriptionId;

    @JsonProperty
    Filters filters;

}

