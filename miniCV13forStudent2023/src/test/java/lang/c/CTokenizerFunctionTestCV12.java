package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerFunctionTestCV12 {

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
    public void funcTest() {
        String testString = """
            func
            void
            return
            call
                """;
        CToken[] expectedTokens = {
            new CToken(CToken.TK_FUNC, 1, 1, "func"),
            new CToken(CToken.TK_VOID, 2, 1, "void"),
            new CToken(CToken.TK_RETURN, 3, 1, "return"),
            new CToken(CToken.TK_CALL, 4, 1, "call"),
        };

        inputStream.setInputString(testString);
        for (int i=0; i<expectedTokens.length; i++) {
            CToken token = tokenizer.getNextToken(cpContext);
            helper.checkToken("token "+(i+1) , token, expectedTokens[i]);
        }
    }
}
