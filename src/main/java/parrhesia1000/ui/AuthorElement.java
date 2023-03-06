package parrhesia1000.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import parrhesia1000.FeedContentElement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.threeten.extra.*;

public class AuthorElement extends HBox {



    public AuthorElement(FeedContentElement feedContentElement) {

        setPadding(new Insets(4,0,4,0));

        Label timeLabel = new Label();

        Duration d = Duration.between(feedContentElement.getCreatedAt(), LocalDateTime.now());
        Duration dd = d.truncatedTo(ChronoUnit.SECONDS);
        String d1 = AmountFormats.wordBased(dd, Locale.getDefault());
        String d2 = humanReadableFormat(d);

        timeLabel.setText(d1);

        getChildren().add(timeLabel);

    }

    public static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }
}
