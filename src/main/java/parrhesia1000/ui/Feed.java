package parrhesia1000.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import parrhesia1000.TimeRange;
import parrhesia1000.ui.control.FeedContentBox;

import java.util.Collection;
import java.util.Collections;


public class Feed {

    private final ObservableList<FeedContentBox> sortedListView;

    private final ObservableList<FeedContentBox> elements = FXCollections.observableArrayList();

    public Feed() {
        this.sortedListView = new SortedList<>(elements,
                (FeedContentBox element1, FeedContentBox element2) -> element2.getFeedContentElement().getCreatedAt().compareTo(element1.getFeedContentElement().getCreatedAt()));
    }

    public void addElement(FeedContentBox feedContentBox) {
        elements.add(feedContentBox);
    }

    public void addElements(Collection<FeedContentBox> feedContentBox) {
        elements.addAll(feedContentBox);
    }

    // Getter //


    public ObservableList<FeedContentBox> getFeedContent() {
        return sortedListView;
    }


}
