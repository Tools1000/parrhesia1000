package parrhesia1000.ui.control;

import fx1000.UiUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;
import parrhesia1000.AuthorCache;
import parrhesia1000.LoadPictureTask;
import parrhesia1000.config.AppConfig;
import parrhesia1000.FeedContentElement;
import parrhesia1000.dto.Author;
import parrhesia1000.ui.UiConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executor;

@Slf4j
public class AuthorBoxElement extends HBox {

    private final TimerTask updateDurationTimerTask = new TimerTask() {
        @Override
        public void run() {
            log.debug("Updating time for {}", feedContentElement.getCreatedAt());
            Platform.runLater(() -> timeLabel.setText(getWordbasedDuration()));
        }
    };

    private final Executor executor;

    private final AppConfig appConfig;

    private final UiConfig uiConfig;

    private final AuthorCache authorCache;

    private final FeedContentElement feedContentElement;

    private final Label timeLabel;



    private final Label displayName = new Label();

    private final Label name = new Label();

    public AuthorBoxElement(Executor executor, AppConfig appConfig, UiConfig uiConfig, AuthorCache authorCache, FeedContentElement feedContentElement) {
        this.executor = executor;
        this.appConfig = appConfig;
        this.uiConfig = uiConfig;
        this.authorCache = authorCache;
        this.feedContentElement = feedContentElement;
        timeLabel = new Label();





        setPadding(new Insets(4, 0, 4, 0));

        setSpacing(4);





        if(feedContentElement.getAuthor() == null) {

            feedContentElement.authorProperty().addListener(new ChangeListener<Author>() {
                @Override
                public void changed(ObservableValue<? extends Author> observable, Author oldValue, Author newValue) {

                    loadAuthorData(newValue);
                }
            });

        }else {
            loadAuthorData(feedContentElement.getAuthor());
        }

        timeLabel.setText(formatDuration(Duration.between(feedContentElement.getCreatedAt(), LocalDateTime.now())));

        getChildren().addAll(UiUtil.applyDebug(displayName, appConfig.isDebug()), UiUtil.applyDebug(name, appConfig.isDebug()), UiUtil.applyDebug(timeLabel, appConfig.isDebug()));

        new Timer(true).schedule(updateDurationTimerTask, 1000 * 10, 1000 * 10);



    }

    private void loadAuthorData(Author author) {
        displayName.setText(author.getDisplayName());
        name.setText("@" + author.getName());
    }

    String getWordbasedDuration(){
        Duration duration = Duration.between(feedContentElement.getCreatedAt(), LocalDateTime.now());
        Duration truncatedDuration = duration.truncatedTo(ChronoUnit.SECONDS);
        return formatDuration(truncatedDuration);
    }

    private String formatDuration(Duration duration) {
        long days = duration.toDaysPart();
        if (days > 0) {
            return days + "d";
        }
        int hours = duration.toHoursPart();
        if (hours > 0) {
            return hours + "h";
        }
        int minutes = duration.toMinutesPart();
        if (minutes > 0) {
            return minutes + "m";
        }
        int seconds = duration.toSecondsPart();
       return seconds + "s";
    }
}
