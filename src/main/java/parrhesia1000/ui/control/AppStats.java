package parrhesia1000.ui.control;

import fx1000.UiUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import parrhesia1000.config.AppConfig;

public class AppStats extends VBox {

    private final IntegerProperty authorCacheSize;

    private final IntegerProperty authorMetadataRequesting;

    public AppStats() {
        setPadding(new Insets(4,4,4,4));
        this.authorCacheSize = new SimpleIntegerProperty();
        this.authorMetadataRequesting = new SimpleIntegerProperty();
        Label keyAuthorCacheSize = new Label("Author cache size");
        Label valueAuthorCacheSize = new Label();
        valueAuthorCacheSize.textProperty().bind(authorCacheSize.asString());
        GridPane pane = new GridPane();
        pane.setHgap(4);
        pane.setVgap(4);
        pane.add(keyAuthorCacheSize, 0, 0);
        pane.add(valueAuthorCacheSize, 1, 0);
        getChildren().add(UiUtil.applyDebug(pane, true));
        Label keyAuthorsRequesting = new Label("Author metadata requesting");
        Label valueAuthorsRequesting = new Label();
        valueAuthorsRequesting.textProperty().bind(authorMetadataRequesting.asString());
        pane = new GridPane();
        pane.setHgap(4);
        pane.setVgap(4);
        pane.add(keyAuthorsRequesting, 0, 0);
        pane.add(valueAuthorsRequesting, 1, 0);
        getChildren().add(UiUtil.applyDebug(pane, true));
    }

    // FX Getter / Setter //


    public int getAuthorCacheSize() {
        return authorCacheSize.get();
    }

    public IntegerProperty authorCacheSizeProperty() {
        return authorCacheSize;
    }

    public void setAuthorCacheSize(int authorCacheSize) {
        this.authorCacheSize.set(authorCacheSize);
    }
}
