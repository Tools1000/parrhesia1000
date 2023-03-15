package parrhesia1000.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
@JsonFormat(shape= JsonFormat.Shape.ARRAY)
public class Request {

    public Request addAuthorsFilter(List<String> authors){
        if(authors != null && !authors.isEmpty()) {
            Filters filters = getFilters();
            if (filters == null) {
                filters = new Filters();
            }
            filters.setAuthors(authors);
            setFilters(filters);
        }
        return this;
    }

    public Request addKindsFilter(List<Integer> kinds){
        if(kinds != null && !kinds.isEmpty()) {
            Filters filters = getFilters();
            if (filters == null) {
                filters = new Filters();
            }
            filters.setKinds(kinds);
            setFilters(filters);
        }
        return this;
    }

    @JsonProperty
    private String req = "REQ";

    @JsonProperty
    private String subscriptionId;

    @JsonProperty
    private Filters filters;

}

