package parrhesia1000;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Filters {

    @JsonProperty("authors")
    private List<String> authors = new ArrayList<>();

    @JsonProperty("kinds")
    private List<Integer> kinds = new ArrayList<>();
}
