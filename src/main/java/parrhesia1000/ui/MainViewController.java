package parrhesia1000.ui;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import parrhesia1000.AppConfig;
import parrhesia1000.ParrhesiaClient;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
@FxmlView("/fxml/MainView.fxml")
public class MainViewController extends DebuggableController implements Initializable {

    private final PersonalFeed personalFeed;

    private final ParrhesiaClient client;
    public VBox listViewBox;
    public VBox root;

    @FXML
    ListView<FeedContentUiElement> listView;

    public MainViewController(AppConfig appConfig, PersonalFeed personalFeed, ParrhesiaClient client) {
        super(appConfig);
        this.personalFeed = personalFeed;
        this.client = client;
    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[0];
    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applyRandomColors(root, listViewBox, listView);
        listView.prefWidthProperty().bind(listViewBox.prefWidthProperty());
        listView.prefHeightProperty().bind(listViewBox.prefHeightProperty());
        listView.setCellFactory(new Callback<ListView<FeedContentUiElement>, ListCell<FeedContentUiElement>>() {
            @Override
            public ListCell<FeedContentUiElement> call(ListView<FeedContentUiElement> param) {
                ListCell<FeedContentUiElement> lc = new ListCell<>() {

                    @Override
                    protected void updateItem(FeedContentUiElement item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                            item.prefWidthProperty().bind(listView.widthProperty().subtract(50));
                        }
                    }
                };
                return lc;
            }
        });
        Bindings.bindContent(listView.getItems(), personalFeed.getFeedContentUiElements());
        log.debug("Initialization done, connecting");
        client.connect();
    }


}
