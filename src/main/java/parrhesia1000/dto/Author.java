package parrhesia1000.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {

    @JsonProperty("display_name")
    private String displayName;


    private String website;

    private String name;

    private String about;

    private String lud16;

    private String pubkey;

    private String banner;
}
