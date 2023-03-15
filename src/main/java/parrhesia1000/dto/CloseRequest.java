package parrhesia1000.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
@JsonFormat(shape= JsonFormat.Shape.ARRAY)
public class CloseRequest {

    @JsonProperty
    private String req = "CLOSE";

    @JsonProperty
    private String subscriptionId;
}
