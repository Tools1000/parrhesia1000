package parrhesia1000;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyBech32ToHexStringTest {

    @Test
    void testDecode() {
        String input = "npub1s5cmkc7lhcsku9qwmpg8kwr83a5y4en7psr9z247tgv72rcdk8psy7c27z";
        String output = Bech32ToHexString.decode(input);
        assertEquals("8531bb63dfbe216e140ed8507b38678f684ae67e0c06512abe5a19e50f0db1c3", output);
    }






}