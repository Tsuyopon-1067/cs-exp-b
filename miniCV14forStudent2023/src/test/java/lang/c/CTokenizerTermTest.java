package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerTermTest {
    
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
    public void multNumber() {
        String testString = "*100";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_MULT, "*", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "100", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_EOF, "end_of_file", 1, 5);
    }

    @Test
    public void divNumber() {
        String testString = "/100";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_DIV, "/", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "100", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_EOF, "end_of_file", 1, 5);
    }

    @Test
    public void termTokenWithPARTest() {
        String testString = "(1+2)*3/-(4-5)";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_LPAR, "(", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "1", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_PLUS, "+", 1, 3);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_NUM, "2", 1, 4);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_RPAR, ")", 1, 5);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_MULT, "*", 1, 6);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_NUM, "3", 1, 7);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_DIV, "/", 1, 8);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_MINUS, "-", 1, 9);
        CToken token10 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 10", token10, CToken.TK_LPAR, "(", 1, 10);
        CToken token11 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 11", token11, CToken.TK_NUM, "4", 1, 11);
        CToken token12 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 12", token12, CToken.TK_MINUS, "-", 1, 12);
        CToken token13 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 13", token13, CToken.TK_NUM, "5", 1, 13);
        CToken token14 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 14", token14, CToken.TK_RPAR, ")", 1, 14);
        CToken token15 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 15", token15, CToken.TK_EOF, "end_of_file", 1, 15);
    }

    @Test
    public void termTokenExpressionTest() {
        String testString = "+4--5++2";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_PLUS, "+", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "4", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_MINUS, "-", 1, 3);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_MINUS, "-", 1, 4);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_NUM, "5", 1, 5);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_PLUS, "+", 1, 6);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_PLUS, "+", 1, 7);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_NUM, "2", 1, 8);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_EOF, "end_of_file", 1, 9);
    }    

    // ブロックコメントアウトが閉じられていないと、EOFが来てしまうハズ
    @Test
    public void termNotCloseBlockComment() {
        String testString = "(1+2)/*3+4";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_LPAR, "(", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "1", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_PLUS, "+", 1, 3);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_NUM, "2", 1, 4);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_RPAR, ")", 1, 5);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void termNotCloseBlockCommentTwoTimes() {
        String testString = "(1/*+2)/*3+4";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_LPAR, "(", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "1", 1, 2);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_EOF, "end_of_file", 1, 3);
    }

}
