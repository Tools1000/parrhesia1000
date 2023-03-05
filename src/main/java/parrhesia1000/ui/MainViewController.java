package parrhesia1000.ui;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import parrhesia1000.AppConfig;

@Slf4j
@Component
@FxmlView("/fxml/MainView.fxml")
public class MainViewController extends DebuggableController implements Initializable {

    public MainViewController(AppConfig appConfig) {
        super(appConfig);
    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[0];
    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
    }
}
