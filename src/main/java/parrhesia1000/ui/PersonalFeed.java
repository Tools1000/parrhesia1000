package parrhesia1000.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import lombok.Getter;
import org.springframework.stereotype.Component;
import parrhesia1000.ui.FeedContentUiElement;

@Getter
@Component
public class PersonalFeed {

    private final ObservableList<FeedContentUiElement> feedContentUiElements;

    private final ObservableList<FeedContentUiElement> observableList = FXCollections.observableArrayList();

    public PersonalFeed() {
        SortedList<FeedContentUiElement> sortedList = new SortedList<>( observableList,
                (FeedContentUiElement stock1, FeedContentUiElement stock2) -> {
                    return stock2.getFeedContentElement().getCreatedAt().compareTo(stock1.getFeedContentElement().getCreatedAt());
                });
        this.feedContentUiElements = sortedList;
    }

    public void addElement(FeedContentUiElement feedContentUiElement){
        observableList.add(feedContentUiElement);
    }
}
