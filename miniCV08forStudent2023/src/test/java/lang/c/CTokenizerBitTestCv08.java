package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerBitTestCv08 {

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
    public void bit() {
        String testString = "!(true && false || true && false)";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NOT, "!", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_LPAR, "(", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_TRUE, "true", 1, 3);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_AND, "&&", 1, 8);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_FALSE, "false", 1, 11);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_OR, "||", 1, 17);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_TRUE, "true", 1, 20);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_AND, "&&", 1, 25);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_FALSE, "false", 1, 28);
        CToken token10 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 10", token10, CToken.TK_RPAR, ")", 1, 33);
        CToken token11 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 11", token11, CToken.TK_EOF, "end_of_file", 1, 34);
    }
}
