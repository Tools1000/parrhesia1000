package parrhesia1000;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormattedTextTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test01(){
        String s = "hans peter wurst ";
        FormattedText text = new FormattedText(s);
        System.out.println(text.getEntries());
    }
}