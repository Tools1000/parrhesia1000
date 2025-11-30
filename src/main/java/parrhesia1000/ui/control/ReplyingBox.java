package parrhesia1000.ui.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import parrhesia1000.dto.Author;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReplyingBox extends HBox {

    static class Entry {

        public Entry(String pub) {
            this.pub = pub;
        }

        String pub;
        ObjectProperty<Author> author = new SimpleObjectProperty<>();
    }

    private final Label label;

    private ObservableList<Entry> pubs;

    private final HBox values = new HBox(4);

    public ReplyingBox() {
        this.pubs = FXCollections.observableArrayList();
        this.label = new Label("Replying to ");
        label.managedProperty().bind(label.visibleProperty());
        values.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                label.setVisible(!c.getList().isEmpty());
            }
        });
        pubs.addListener(new ListChangeListener<Entry>() {
            @Override
            public void onChanged(Change<? extends Entry> c) {
                while(c.next()){
                    addToValues(c.getAddedSubList());
                }
            }
        });
        getChildren().addAll(label, values);
    }

    private void addToValues(List<? extends Entry> addedSubList) {
        for(Entry s : addedSubList) {
            if(s.author.get() == null) {
                values.getChildren().add(new Label(StringUtils.abbreviateMiddle(s.pub, "..", 10)));
                s.author.addListener(new ChangeListener<Author>() {
                    @Override
                    public void changed(ObservableValue<? extends Author> observable, Author oldValue, Author newValue) {
                        if(newValue != null){
                            values.getChildren().add(new Label(newValue.getDisplayName()));
                        }
                    }
                });
            }else {
                values.getChildren().add(new Label(s.author.getValue().getDisplayName()));
            }
        }
    }

    public void setPubIds(List<List<String>> replyingPubs) {
        for (List<String> list : replyingPubs) {
            if (list.size() == 2 && list.get(0).equals("p")) {
                pubs.add(new Entry(list.get(1)));
            }
        }
    }

    public void updateAuthor(Author author) {
        log.debug("Update author with {}", author);
        for(Entry e : pubs){
            if(e.pub.equals(author.getPubkey())){
                e.author.set(author);
            }
        }
    }
}
