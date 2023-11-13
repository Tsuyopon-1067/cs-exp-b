package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerExpressionSubTest {

    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    IOContext context;
    CTokenizer tokenizer;
    CParseContext cpContext;
    CTokenizerTestHelper helper;

    @Before
    public void setUp() {
        inputStream = new InputStreamForTest();
        outputStream = new PrintStreamForTest(System.out);
        errorOutputStream = new PrintStreamForTest(System.err);
        context = new IOContext(inputStream, outputStream, errorOutputStream);
        tokenizer = new CTokenizer(new CTokenRule());
        cpContext = new CParseContext(context, tokenizer);
        helper = new CTokenizerTestHelper();
    }

    @After
    public void tearDown() {
        inputStream = null;
        outputStream = null;
        errorOutputStream = null;
        context = null;
        tokenizer = null;
        cpContext = null;
        helper = null;
    }

    @Test
    public void minusNumber() {
        String testString = "-100";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_MINUS, "-", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "100", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_EOF, "end_of_file", 1, 5);
    }

    @Test
    public void readSimpleTokenSub() {
        inputStream.setInputString("13 + 7 - 2");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "13", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_PLUS, "+", 1, 4);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_NUM, "7", 1, 6);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_MINUS, "-", 1, 8);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_NUM, "2", 1, 10);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_EOF, "end_of_file", 1, 11);
    }

}
