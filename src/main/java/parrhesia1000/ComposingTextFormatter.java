package parrhesia1000;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.util.List;


public class ComposingTextFormatter implements TextFormatter {

    private final HostServices hostServices;

    private final List<TextFormatterStrategy> strategies;

    public ComposingTextFormatter(HostServices hostServices) {
        this(hostServices, List.of(new HyperLinkFormatterStrategy(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Hyperlink source = (Hyperlink) event.getSource();
                hostServices.showDocument(source.getText());
            }
        })));
    }

    public ComposingTextFormatter(HostServices hostServices, List<TextFormatterStrategy> strategies) {
        this.hostServices = hostServices;
        this.strategies = strategies;
    }

    @Override
    public FormattedText format(FormattedText text) {
        FormattedText result = text;
        for (TextFormatterStrategy s : strategies) {
            result = s.format(text);
        }
        for(FormattedText.Entry entry : result.getEntries()){
            if(entry.getNode() == null){
                entry.setNode(new Text(entry.getText()));
            }
        }
        return result;
    }
}
