package parrhesia1000.ui.control;

import fx1000.UiUtil;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import parrhesia1000.*;
import parrhesia1000.config.AppConfig;
import parrhesia1000.dto.Author;
import parrhesia1000.ui.UiConfig;

import java.util.concurrent.Executor;

@Getter
public class FeedContentBox extends VBox {

    private final FeedContentElement feedContentElement;

    private final TextFormatter textFormatter;

    private final Executor executor;

    private final AppConfig appConfig;

    private final UiConfig uiConfig;

    private final HBox imageBox = new HBox();

    public FeedContentBox(Executor executor, AppConfig appConfig, UiConfig uiConfig, HostServices hostServices, FeedContentElement feedContentElement, AuthorCache authorCache) {

        this.feedContentElement = feedContentElement;
        this.textFormatter = new ComposingTextFormatter(hostServices);
        this.executor = executor;
        this.appConfig = appConfig;
        this.uiConfig = uiConfig;
        imageBox.setPadding(new Insets(2, 2, 2, 2));



        VBox content = new VBox(4);

        AuthorBoxElement authorBoxElement = new AuthorBoxElement(executor, appConfig, uiConfig, authorCache, feedContentElement);

        FormattedText text = new FormattedText(feedContentElement.getText());

        FormattedText text2 = textFormatter.format(text);



        content.getChildren().add(UiUtil.applyDebug(authorBoxElement, appConfig.isDebug()));

        content.getChildren().add(new TextFlow(text2.getEntries().stream().map(FormattedText.Entry::getNode).toArray(Node[]::new)));

        if (feedContentElement.getAuthor() == null) {

            feedContentElement.authorProperty().addListener(new ChangeListener<Author>() {
                @Override
                public void changed(ObservableValue<? extends Author> observable, Author oldValue, Author newValue) {
                    loadAuthorData(newValue);
                }
            });

        } else {
            loadAuthorData(feedContentElement.getAuthor());
        }


        HBox imageAndContentBox = new HBox(4);
        imageAndContentBox.getChildren().add(UiUtil.applyDebug(imageBox, appConfig.isDebug()));
        imageAndContentBox.getChildren().add(UiUtil.applyDebug(content, appConfig.isDebug()));

        getChildren().add(UiUtil.applyDebug(imageAndContentBox, appConfig.isDebug()));

    }

    private void loadAuthorData(Author author) {
        String pic = author.getPicture();
        executor.execute(new LoadPictureTask(pic, imageBox, appConfig.isDebug(), uiConfig.getAuthorCircleRadius()));
    }
}