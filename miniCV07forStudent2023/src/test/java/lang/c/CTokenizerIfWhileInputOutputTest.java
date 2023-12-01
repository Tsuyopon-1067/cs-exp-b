package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerIfWhileInputOutputTest {

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
    public void statementIf() {
        String testString = "if";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_IF, "if", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 3);
    }

    @Test
    public void statementElse() {
        String testString = "else";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_ELSE, "else", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 5);
    }

    @Test
    public void statementWhile() {
        String testString = "while";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_WHILE, "while", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void statementInput() {
        String testString = "input";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_INPUT, "input", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void statementOUTPUT() {
        String testString = "output";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_OUTPUT, "output", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 7);
    }

    @Test
    public void statementLRCAR() {
        String testString = "{}";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_LCUR, "{", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token2, CToken.TK_RCUR, "}", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_EOF, "end_of_file", 1, 3);
    }

    // Please copy and paste the above code and add the specified test case to the following
    // if{else}endif;while(input)output
    @Test
    public void statementAll() {
        String testString = "if{else}endif;while(input)output";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_IF, "if", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_LCUR, "{", 1, 3);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_ELSE, "else", 1, 4);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_RCUR, "}", 1, 8);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_IDENT, "endif", 1, 9);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_SEMI, ";", 1, 14);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_WHILE, "while", 1, 15);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_LPAR, "(", 1, 20);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_INPUT, "input", 1, 21);
        CToken token10 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 10", token10, CToken.TK_RPAR, ")", 1, 26);
        CToken token11 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 11", token11, CToken.TK_OUTPUT, "output", 1, 27);
        CToken token12 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 12", token12, CToken.TK_EOF, "end_of_file", 1, 33);
    }

}