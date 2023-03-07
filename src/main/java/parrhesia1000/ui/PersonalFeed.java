package parrhesia1000.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PersonalFeed {

    private final ObservableList<FeedContentBox> feedContentBoxes;

    private final ObservableList<FeedContentBox> observableList = FXCollections.observableArrayList();

    public PersonalFeed() {
        SortedList<FeedContentBox> sortedList = new SortedList<>(observableList,
                (FeedContentBox stock1, FeedContentBox stock2) -> {
                    return stock2.getFeedContentElement().getCreatedAt().compareTo(stock1.getFeedContentElement().getCreatedAt());
                });
        this.feedContentBoxes = sortedList;
    }

    public void addElement(FeedContentBox feedContentBox) {
        observableList.add(feedContentBox);
    }
}
