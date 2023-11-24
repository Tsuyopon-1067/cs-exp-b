package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerAssignTest {

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
    public void assign() {
        inputStream.setInputString("=");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_ASSIGN, "=", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 2);
    }

    @Test
    public void semi_colon() {
        inputStream.setInputString(";");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_SEMI, ";", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 2);
    }

    @Test
    public void assignWithBlockComment() {
        String testString = "i_a/*comment*/=4";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, 11, "i_a", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, 12, "=", 1, 15);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, 1, "4", 1, 16);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_SEMI, ";", 1, 17);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_EOF, "end_of_file", 1, 18);
    }
}
