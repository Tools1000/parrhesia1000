package parrhesia1000.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.HostServices;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.FeedContentElement;
import parrhesia1000.ui.FeedContentUiElement;
import parrhesia1000.ui.PersonalFeed;

@Slf4j
@Component
public class SubscribedAuthorsEventHandler extends EventHandlerPrototype {

    private final ApplicationContext applicationContext;

    private final PersonalFeed personalFeed;



    public SubscribedAuthorsEventHandler(ObjectMapper objectMapper, ApplicationContext applicationContext, PersonalFeed personalFeed) {
        super(objectMapper);
        this.applicationContext = applicationContext;
        this.personalFeed = personalFeed;
    }

    @Override
    protected void doHandleEvent(WebSocketSession session, Event event) {
        log.debug("Subscribing to author: {}", event);
        Platform.runLater(() -> addContent(event));

    }

    private void addContent(Event event) {
        personalFeed.addElement(new FeedContentUiElement(applicationContext.getBean(HostServices.class), new FeedContentElement(event)));
    }


    @Override
    protected boolean acceptedEvent(String subscriptionId) {
        return "subscribed-authors-feed".equals(subscriptionId);
    }
}
