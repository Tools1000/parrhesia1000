package parrhesia1000.ui.control;

import fx1000.UiUtil;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.*;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import parrhesia1000.nostr.event.Event;
import parrhesia1000.nostr.event.EventParser;
import parrhesia1000.ui.UiConfig;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Getter
public class FeedContentBox extends VBox implements AuthorCache.Listener {

    private final FeedContentElement feedContentElement;

    private final TextFormatter textFormatter;

    private final Executor executor;

    private final AppConfig appConfig;

    private final UiConfig uiConfig;

    private final AuthorCache authorCache;

    private final WebSocketSession session;

    private final HBox imageBox = new HBox(4);

    private final ReplyingBox replyingBox = new ReplyingBox();

    public FeedContentBox(Executor executor, AppConfig appConfig, UiConfig uiConfig, HostServices hostServices, FeedContentElement feedContentElement, AuthorCache authorCache, WebSocketSession session) {

        this.feedContentElement = feedContentElement;
        this.textFormatter = new ComposingTextFormatter(hostServices);
        this.executor = executor;
        this.appConfig = appConfig;
        this.uiConfig = uiConfig;
        this.authorCache = authorCache;
        this.session = session;
        this.authorCache.getListenerList().add(this);


        imageBox.setPadding(new Insets(2, 2, 2, 2));

        VBox content = new VBox(4);

        AuthorBoxElement authorBoxElement = new AuthorBoxElement(executor, appConfig, uiConfig, authorCache, feedContentElement);

        content.getChildren().add(UiUtil.applyDebug(authorBoxElement, appConfig.isDebug()));

        replyingBox.setPubIds(EventParser.getReplyingPubs(feedContentElement.getEvent()));

        content.getChildren().add(replyingBox);

        content.getChildren().add(new TextFlow(textFormatter.format(new FormattedText(feedContentElement.getText())).getEntries().stream().map(FormattedText.Entry::getNode).toArray(Node[]::new)));

        if (feedContentElement.getAuthor() == null) {
            feedContentElement.authorProperty().addListener(this::authorListener);
        } else {
            loadAuthorPicture(feedContentElement.getAuthor());
        }


        HBox imageAndContentBox = new HBox(4);
        imageAndContentBox.getChildren().add(UiUtil.applyDebug(imageBox, appConfig.isDebug()));
        imageAndContentBox.getChildren().add(UiUtil.applyDebug(content, appConfig.isDebug()));

        getChildren().add(UiUtil.applyDebug(imageAndContentBox, appConfig.isDebug()));

    }



    private void authorListener(ObservableValue<? extends Author> observable, Author oldValue, Author newValue) {
        if(newValue != null) {
            loadAuthorPicture(newValue);
        }
        else {
            log.warn("Lost author info");
        }
    }

    private void updateReplies(Author author) {
        log.debug("Got new author info, update replies: {}", author);
        replyingBox.updateAuthor(author);
    }

    private void loadAuthorPicture(Author author) {
        executor.execute(new LoadPictureTask(author.getPicture(), imageBox, appConfig.isDebug(), uiConfig.getAuthorCircleRadius()));
    }

    @Override
    public void newAuthor(Author author) {
        updateReplies(author);

    }
}