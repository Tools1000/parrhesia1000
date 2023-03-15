package parrhesia1000;

import javafx.scene.Node;
import javafx.scene.text.Text;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Getter
public class FormattedText {


    @Getter
    @Setter
    public static class Entry {
        private String text;
        private Node node;

        public Entry(String text, Node node) {
            this.text = text;
            this.node = node;
        }

        public Entry(String text) {
            this.text = text;
        }
    }

    private final List<Entry> entries;

    public FormattedText() {
        this.entries = new ArrayList<>();
    }

    public FormattedText(String text) {
        this();
        entries.add(new Entry(text, new Text(text)));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        Iterator<Entry> it = entries.iterator();
        while(it.hasNext()){
            Entry next = it.next();
            sb.append(next.getText());
            if(it.hasNext()){
                sb.append(",");
            }
        }
        sb.append("\n]");
        return sb.toString();
    }
}
