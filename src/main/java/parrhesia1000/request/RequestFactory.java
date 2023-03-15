package parrhesia1000.request;

import lombok.extern.slf4j.Slf4j;
import parrhesia1000.ParrhesiaUtils;
import parrhesia1000.TimeRange;
import parrhesia1000.dto.CloseRequest;
import parrhesia1000.dto.Filters;
import parrhesia1000.dto.Request;

import java.util.List;

@Slf4j
public class RequestFactory {

    public static String FIND_AUTHORS_SUBSCRIPTION_ID = "find-authors";

    public static String GET_AUTHORS_METADATA = "get-authors-metadata";

    public static String GET_AUTHOR_METADATA = "get-author-metadata";

    public static String SUBSCRIBED_AUTHORS_FEED = "subscribed-authors-feed";

    public static String GLOBAL_FEED = "public-feed";

    public static String PERSONAL_FEED = "personal-feed";

    public static Request buildFindAuthorsRequest(String pub) {
        Filters filters = new Filters();
        filters.setAuthors(List.of(pub));
        filters.setKinds(List.of(3));
        Request req = new Request();
        req.setSubscriptionId(FIND_AUTHORS_SUBSCRIPTION_ID);
        req.setFilters(filters);
        return req;
    }

    public static Request buildGetAuthorsMetadataRequest(List<String> autors) {
        Filters filters = new Filters();
        filters.setAuthors(autors);
        filters.setKinds(List.of(0));
        Request req = new Request();
        req.setSubscriptionId(GET_AUTHORS_METADATA);
        req.setFilters(filters);
        return req;
    }

    public static Request buildGetAuthorMetadataRequest(String autor) {
        Filters filters = new Filters();
        filters.setAuthors(List.of(autor));
        filters.setKinds(List.of(0));
        Request req = new Request();
        req.setSubscriptionId(GET_AUTHOR_METADATA);
        req.setFilters(filters);
        return req;
    }

    public static Request buildSubscribeAuthorsRequest(List<String> authors) {
        Filters filters = new Filters();
        filters.setKinds(List.of(1));
        filters.setAuthors(authors);
        Request req = new Request();
        req.setSubscriptionId(SUBSCRIBED_AUTHORS_FEED);
        req.setFilters(filters);
        return req;
    }

    public static CloseRequest stopRequestRequest(String subscriptionId) {
        CloseRequest req = new CloseRequest();
        req.setSubscriptionId(subscriptionId);
        return req;
    }

    public static Request buildPersonalFeedRequest(String subscriptionId, List<String> authors, int lookBehindSeconds) {

        long currentSeconds = System.currentTimeMillis() / 1000;

        Filters filters = new Filters();
        filters.setAuthors(authors);
        filters.setKinds(List.of(1));
        filters.setSince((int) currentSeconds - lookBehindSeconds);
        Request req = new Request();
        req.setSubscriptionId(subscriptionId);
        req.setFilters(filters);
        return req;
    }





    public static Request buildPublicFeedRequest(int lookBehindSeconds) {

        long currentSeconds = System.currentTimeMillis() / 1000;

        Filters filters = new Filters();
        filters.setKinds(List.of(1));
        filters.setSince((int) currentSeconds - lookBehindSeconds);
        Request req = new Request();
        req.setSubscriptionId(GLOBAL_FEED);
        req.setFilters(filters);
        return req;
    }

    public static Request buildFeedRequest(String subscriptionId, TimeRange range) {


        Filters filters = new Filters();
        filters.setSince((int) ParrhesiaUtils.getSecondsTimestampFromLocalDateTime(range.getEnd()));
        filters.setUntil((int) ParrhesiaUtils.getSecondsTimestampFromLocalDateTime(range.getStart()));
        Request req = new Request();
        req.setSubscriptionId(subscriptionId);
        req.setFilters(filters);
        return req;
    }


}
