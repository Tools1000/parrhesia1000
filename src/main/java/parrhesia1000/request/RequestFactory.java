package parrhesia1000.request;

import parrhesia1000.Filters;

public class RequestFactory {

    public static String FIND_AUTHORS_SUBSCRIPTION_ID = "find-authors";

    public Request buildFindAuthorsRequest(String pub){
        Filters filters = new Filters();
        filters.getAuthors().add(pub);
        filters.getKinds().add(3);
        Request req = new Request();
        req.subscriptionId = FIND_AUTHORS_SUBSCRIPTION_ID;
        req.filters = filters;
        return req;
    }
}
