package parrhesia1000;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class EventCache {

    private final ListProperty<FeedContentElement> elements;

    public EventCache() {
        this.elements = new SimpleListProperty<>(FXCollections.synchronizedObservableList(FXCollections.observableArrayList()));

    }

    // FX Getter / Setter //

    public ObservableList<FeedContentElement> getElements() {
        return elements.get();
    }

    public ListProperty<FeedContentElement> elementsProperty() {
        return elements;
    }

    public void setElements(ObservableList<FeedContentElement> elements) {
        this.elements.set(elements);
    }
}
