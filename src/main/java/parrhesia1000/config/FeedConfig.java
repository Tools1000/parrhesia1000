package parrhesia1000.config;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FeedConfig {

    private long delay;

    private FeedInstanceConfig global;

    private FeedInstanceConfig personal;
}
