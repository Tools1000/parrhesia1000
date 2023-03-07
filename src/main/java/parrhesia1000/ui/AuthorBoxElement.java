package parrhesia1000.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.apache.commons.io.IOUtils;
import org.threeten.extra.AmountFormats;
import parrhesia1000.FeedContentElement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

public class AuthorBoxElement extends HBox {


    private final TimerTask updateDurationTimerTask = new TimerTask() {
        @Override
        public void run() {
            Duration duration = Duration.between(feedContentElement.getCreatedAt(), LocalDateTime.now());
            Duration truncatedDuration = duration.truncatedTo(ChronoUnit.SECONDS);
            String wordBasedDuration = AmountFormats.wordBased(truncatedDuration, Locale.getDefault());
            String humanReadableDuration = humanReadableFormat(truncatedDuration);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    timeLabel.setText(wordBasedDuration);
                }
            });
        }
    };

    private final Executor executor;

    private final FeedContentElement feedContentElement;

    private final Label timeLabel;


    public AuthorBoxElement(Executor executor, FeedContentElement feedContentElement) {
        this.executor = executor;

        this.feedContentElement = feedContentElement;
        timeLabel = new Label();

        setPadding(new Insets(4, 0, 4, 0));

        setSpacing(4);

        Label displayName = new Label(feedContentElement.getAuthor().getDisplayName());
        Label name = new Label("@" + feedContentElement.getAuthor().getName());

        timeLabel.setText("now");


        getChildren().addAll(displayName, name, timeLabel);

        new Timer().schedule(updateDurationTimerTask, 1000 * 10);

        String poster = feedContentElement.getAuthor().getBanner();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try (InputStream in = new URL(poster).openStream()) {
                    byte[] targetArray = IOUtils.toByteArray(in);
                    Image image = new Image(new ByteArrayInputStream(targetArray));
                    image.exceptionProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null)
                            System.err.println("Failed to load image " + newValue);
                    });

                    ImageView imageView = new ImageView();
                    imageView.setFitHeight(40);
                    imageView.setImage(image);
                    imageView.setPreserveRatio(true);

                    Platform.runLater(() -> {


                        getChildren().add(0, imageView);

                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}
