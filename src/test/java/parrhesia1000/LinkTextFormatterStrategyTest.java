package parrhesia1000;

import javafx.scene.Node;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkTextFormatterStrategyTest {


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void format01() {
        FormattedText text = new FormattedText("hans wurst http://google.de");

        FormattedText newText = new LinkTextFormatterStrategy() {

            @Override
            protected Node getUrlNode(String urlString) {
                return null;
            }
        }.format(text);
        assertEquals(2, newText.getEntries().size());
    }

    @Test
    void format02() {
        FormattedText text = new FormattedText("hans wurst http://google.de peter www.test.de");

        FormattedText newText = new LinkTextFormatterStrategy() {
            @Override
            protected Node getUrlNode(String urlString) {
                return null;
            }
        }.format(text);
        assertEquals(4, newText.getEntries().size());
    }

    @Test
    void format03() {
        FormattedText text = new FormattedText("hans wurst");

        FormattedText newText = new LinkTextFormatterStrategy() {
            @Override
            protected Node getUrlNode(String urlString) {
                return null;
            }
        }.format(text);
        assertEquals(1, newText.getEntries().size());
    }

    @Test
    void format04() {
        FormattedText text = new FormattedText("www.google.de");

        FormattedText newText = new LinkTextFormatterStrategy() {
            @Override
            protected Node getUrlNode(String urlString) {
                return null;
            }
        }.format(text);
        assertEquals(1, newText.getEntries().size());
    }
}