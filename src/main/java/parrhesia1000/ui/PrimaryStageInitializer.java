package parrhesia1000.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ResourceBundle;

@RequiredArgsConstructor
@Slf4j
@Component
public class PrimaryStageInitializer {

    private final FxWeaver fxWeaver;

    private final UiConfig uiConfig;

    private final ResourceBundle resourceBundle;

    private Scene mainScene;

    @EventListener
    public void onApplicationEvent(StageReadyEvent event) {
        Platform.runLater(() -> {
            Stage stage = event.stage();
            mainScene = new Scene(fxWeaver.loadView(MainViewController.class, resourceBundle), uiConfig.getInitialWidth(), uiConfig.getInitialHeight());
            mainScene.getStylesheets().add("/css/general.css");
            stage.setTitle(uiConfig.getAppTitle());
            mainScene.setFill(Color.TRANSPARENT);
            stage.setScene(mainScene);
            stage.show();
        });
    }

    public Scene getMainScene() {
        return mainScene;
    }
}
