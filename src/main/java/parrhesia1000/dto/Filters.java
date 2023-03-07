package parrhesia1000.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Filters {

    @JsonProperty("authors")
    private List<String> authors;

    @JsonProperty("kinds")
    private List<Integer> kinds;

    @JsonProperty("ids")
    private List<String> ids;
}
