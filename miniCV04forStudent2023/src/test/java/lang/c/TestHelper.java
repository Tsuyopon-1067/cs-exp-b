package lang.c;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lang.FatalErrorException;
import lang.PrintStreamForTest;

public class TestHelper {

    /**
     * ロバストな比較用にコメント行なし・コメント部分なし・空白/タブなしのデータに変換する
     * @param data 処理対象の文字列のリスト
     * @return コメント行・コメント部分・空白/タブ削除済の文字列のリスト
     */
    LinkedList<String> convertToValidateData(LinkedList<String> data) {
        LinkedList<String> dataForValidate = new LinkedList<String>();
        for ( String line: data ) {
            String lineNoComment = line.replaceAll(";.*$", "");
            String lineTrimed = lineNoComment.trim();
            String lineNoBlankCharacter = lineTrimed.replaceAll("[ \t]", "");
            if ( lineNoBlankCharacter.length() == 0 ) {
                continue;
            }
            dataForValidate.add(lineNoBlankCharacter);
        }
        return dataForValidate;
    }

    // Check only code portion, not validate comments
    public void checkCodePortion(List<String> actual, String[] expected) {
        LinkedList<String> expectedForValidate = convertToValidateData(new LinkedList<String>(Arrays.asList(expected)));
        LinkedList<String> actualForValidate = convertToValidateData(new LinkedList<String>(actual));

        assertThat("Actual " + actualForValidate.toString() + "  Line Size: ", actualForValidate.size(), is(expectedForValidate.size()));

        int actualNoCommentPos = 0;
        for (int i = 0; i < expectedForValidate.size(); i++) {
            String message = "Line: " + String.valueOf(i);

            // Remove comment area, and blank characters at the head and/or tail.
            String expectedCode = expectedForValidate.get(i);
            String actualCode = actualForValidate.get(actualNoCommentPos);
            actualNoCommentPos++;

            assertThat(message, actualCode, is(expectedCode));
        }
    }

    public void checkCodeGen(String[] expected, CParseRule parseRule, CParseContext cpContext)
            throws FatalErrorException {
        PrintStreamForTest outputStream = (PrintStreamForTest) cpContext.getIOContext().getOutStream();
        PrintStreamForTest errorOutputStream = (PrintStreamForTest) cpContext.getIOContext().getErrStream();
        cpContext.getTokenizer().getNextToken(cpContext);
        parseRule.parse(cpContext);
        parseRule.semanticCheck(cpContext);

        // call test target
        parseRule.codeGen(cpContext);

        // Check finished without errors
        String errorOutput = errorOutputStream.getPrintBufferString();
        assertThat(errorOutput, is(""));

        // Check only code portion, not validate comments
        List<String> outputBuffer = outputStream.getPrintBuffer();
        checkCodePortion(outputBuffer, expected);
    }

    public void checkCodeGen(String expected, CParseRule parseRule, CParseContext cpContext)
            throws FatalErrorException {
        String[] expectedList = expected.split("[\n\r]");
        checkCodeGen(expectedList, parseRule, cpContext);
    }
}
