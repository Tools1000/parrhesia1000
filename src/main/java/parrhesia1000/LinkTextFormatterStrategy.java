package parrhesia1000;

import javafx.scene.Node;
import javafx.scene.text.Text;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class LinkTextFormatterStrategy implements TextFormatterStrategy {

    // Pattern for recognizing a URL, based off RFC 3986
    static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @Override
    public FormattedText format(FormattedText words) {
        log.debug("Input text:\n{}", words);
        FormattedText newText = new FormattedText();

        for (FormattedText.Entry element : words.getEntries()) {
            String text = element.getText();
            Matcher matcher = urlPattern.matcher(text);
            int offset = 0;
            String tailingString = "";
            String leadingString = text;
            boolean noMatches = true;
            while(matcher.find()){
                int start = matcher.start();
                int end = matcher.end();
                leadingString = text.substring(offset, start);
                String urlString = text.substring(start, end);
                tailingString = text.substring(end);
                log.debug("Text split into:[{},{},{}]", leadingString, urlString, tailingString);
                if(StringUtils.isNotEmpty(leadingString)){
                    newText.getEntries().add(new FormattedText.Entry(leadingString));
                }
                newText.getEntries().add(new FormattedText.Entry(urlString, getUrlNode(urlString)));
                offset += leadingString.length() + urlString.length();
                noMatches = false;
            }
            if(noMatches && StringUtils.isNotEmpty(leadingString)) {
                newText.getEntries().add(new FormattedText.Entry(leadingString));
            }
            if(StringUtils.isNotEmpty(tailingString)) {
                newText.getEntries().add(new FormattedText.Entry(tailingString));
            }
        }
        log.debug("new text:\n{}", newText);
        return newText;
    }

    protected abstract Node getUrlNode(String urlString);


}
