package lang.c;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CTokenizerTestHelper {
    public void checkToken(String message, CToken token, int type, String text, int lineNo, int columnNo) {
        assertThat(message + ":" + "Type: ", token.getType(), is(type));
        assertThat(message + ":" + "Text: ", token.getText(), is(text));
        assertThat(message + ":" + "LineNo: ", token.getLineNo(), is(lineNo));
        assertThat(message + ":" + "ColumnNo: ", token.getColumnNo(), is(columnNo));
    }
}
