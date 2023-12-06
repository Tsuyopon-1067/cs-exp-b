package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerVariableTestCv10 {

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
    public void varriableTest() {
        String testString = """
            int i_a = 3;
            const int *ip_b = &3;
                """;
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_INT, "int", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_IDENT, "i_a", 1, 5);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_ASSIGN, "=", 1, 9);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_NUM, "3", 1, 11);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_SEMI, ";", 1, 12);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_CONST, "const", 2, 1);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_INT, "int", 2, 7);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_MULT, "*", 2, 11);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_IDENT, "ip_b", 2, 12);
        CToken token10 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 10", token10, CToken.TK_ASSIGN, "=", 2, 17);
        CToken token11 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 11", token11, CToken.TK_AMP, "&", 2, 19);
        CToken token12 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 12", token12, CToken.TK_NUM, "3", 2, 20);
        CToken token13 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 13", token13, CToken.TK_SEMI, ";", 2, 21);
        CToken token14 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 14", token14, CToken.TK_EOF, "end_of_file", 3, 1);
    }

    @Test
    public void variableCommaTest() {
        String testString = """
            int i_a, i_b;
            const int i_c = 3;
                """;
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_INT, "int", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_IDENT, "i_a", 1, 5);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_COMMA, ",", 1, 8);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_IDENT, "i_b", 1, 10);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_SEMI, ";", 1, 13);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_CONST, "const", 2, 1);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_INT, "int", 2, 7);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_IDENT, "i_c", 2, 11);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_ASSIGN, "=", 2, 15);
        CToken token10 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 10", token10, CToken.TK_NUM, "3", 2, 17);
        CToken token11 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 11", token11, CToken.TK_SEMI, ";", 2, 18);
        CToken token12 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 12", token12, CToken.TK_EOF, "end_of_file", 3, 1);
    }
}
