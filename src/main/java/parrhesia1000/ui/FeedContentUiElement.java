package parrhesia1000.ui;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import parrhesia1000.FeedContentElement;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class FeedContentUiElement extends VBox {

    // Pattern for recognizing a URL, based off RFC 3986
    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private final FeedContentElement feedContentElement;

    public FeedContentUiElement(HostServices hostServices, FeedContentElement feedContentElement) {

        this.feedContentElement = feedContentElement;

        AuthorElement authorElement = new AuthorElement(feedContentElement);

        super.getChildren().add(authorElement);

        setPadding(new Insets(8, 8, 8, 8));


        List<Node> textList = new ArrayList<>();


        Matcher matcher = urlPattern.matcher(feedContentElement.getText());

        boolean found = false;

        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();
            // now you have the offsets of a URL match
            String url = feedContentElement.getText().substring(matchStart, matchEnd);
            Text text = new Text(feedContentElement.getText().substring(0, matchStart));
//           text.setFill(Color.color(Math.random(), Math.random(), Math.random()));
            textList.add(text);
            Hyperlink link = new Hyperlink();
            link.setText(feedContentElement.getText().substring(matchStart, matchEnd));
            link.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    hostServices.showDocument(link.getText());
                }
            });
            textList.add(link);
            found = true;
        }

        if (!found) {
            Text text = new Text(feedContentElement.getText());
            textList.add(text);
        }

        TextFlow textFlow = new TextFlow(textList.toArray(new Node[0]));

        super.getChildren().add(textFlow);

    }
}
