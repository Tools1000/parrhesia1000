package parrhesia1000;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HyperLinkFormatterStrategy extends LinkTextFormatterStrategy {

    private final EventHandler<ActionEvent> eventEventHandler;

    @Override
    protected Node getUrlNode(String urlString) {
        Hyperlink result = new Hyperlink(urlString);
        result.setOnAction(eventEventHandler);
        return result;
    }
}
