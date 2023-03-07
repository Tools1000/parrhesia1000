package parrhesia1000.request;

import parrhesia1000.dto.Filters;
import parrhesia1000.dto.Request;

import java.util.List;

public class RequestFactory {

    public static String FIND_AUTHORS_SUBSCRIPTION_ID = "find-authors";

    public static String GET_AUTHORS_METADATA = "get-authors-metadata";

    public static String SUBSCRIBED_AUTHORS_FEED = "subscribed-authors-feed";

    public static Request buildFindAuthorsRequest(String pub) {
        Filters filters = new Filters();
        filters.setAuthors(List.of(pub));
        filters.setKinds(List.of(3));
        Request req = new Request();
        req.setSubscriptionId(FIND_AUTHORS_SUBSCRIPTION_ID);
        req.setFilters(filters);
        return req;
    }

    public static Request buildGetAuthorMetadataRequest(List<String> autors) {
        Filters filters = new Filters();
        filters.setAuthors(autors);
        filters.setKinds(List.of(0));
        Request req = new Request();
        req.setSubscriptionId(GET_AUTHORS_METADATA);
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


}
