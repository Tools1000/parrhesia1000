package parrhesia1000;

import fx1000.UiUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
public class LoadPictureTask extends Task<Void> {

    private final String pic;

    private final Pane imageBox;

    private final boolean debug;

    private final int authorCircleRadius;

    @Override
    protected Void call() throws Exception {
        if (StringUtils.hasText(pic)) {
            try (InputStream in = new URL(pic).openStream()) {
                byte[] targetArray = IOUtils.toByteArray(in);
                if (targetArray.length > 0) {
                    Platform.runLater(() -> {
                        try {
                            Image image = new Image(new ByteArrayInputStream(targetArray));
                            image.exceptionProperty().addListener((observable2, oldValue2, newValue2) -> {
                                if (newValue2 != null) {
                                    log.debug("Failed to load image", newValue2);

                                }
                            });
                            if (!image.isError()) {
                                Circle circle = new Circle(authorCircleRadius);
                                ImagePattern pattern = new ImagePattern(image);
                                circle.setFill(pattern);
                                imageBox.getChildren().add(UiUtil.applyDebug(circle, debug));
                            }
                        } catch (Exception e) {
                            log.debug(e.toString());
                        }
                    });
                }
            } catch (Exception e) {
                log.debug(e.toString());
            }
        }
        return null;
    }
}
