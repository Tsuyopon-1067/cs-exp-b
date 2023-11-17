package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerIdentTest {

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
    public void ident() {
        inputStream.setInputString("vAriable_1");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_IDENT, "vAriable_1", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 11);
    }

    @Test
    public void blacket() {
        inputStream.setInputString("[128]");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_LBRA, "[", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "128", 1, 2);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_RBRA, "]", 1, 5);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void cutOutIdentifier() {
        inputStream.setInputString("abc+-def[ghij+*k]lmn");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_IDENT, "abc", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_PLUS, "+", 1, 4);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_MINUS, "-", 1, 5);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_IDENT, "def", 1, 6);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_LBRA, "[", 1, 9);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_IDENT, "ghij", 1, 10);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_PLUS, "+", 1, 14);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_MULT, "*", 1, 15);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_IDENT, "k", 1, 16);
        CToken token10 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 10", token10, CToken.TK_RBRA, "]", 1, 17);
        CToken token11 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 11", token11, CToken.TK_IDENT, "lmn", 1, 18);
        CToken token12 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 12", token12, CToken.TK_EOF, "end_of_file", 1, 21);
    }

    @Test
    public void cutOutFactors() {
        inputStream.setInputString("ab0c+-1def[gh2ij+*k3]4lm5n");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_IDENT, "ab0c", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_PLUS, "+", 1, 5);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_MINUS, "-", 1, 6);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_NUM, "1", 1, 7);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_IDENT, "def", 1, 8);
        CToken token6 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 6", token6, CToken.TK_LBRA, "[", 1, 11);
        CToken token7 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 7", token7, CToken.TK_IDENT, "gh2ij", 1, 12);
        CToken token8 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 8", token8, CToken.TK_PLUS, "+", 1, 17);
        CToken token9 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 9", token9, CToken.TK_MULT, "*", 1, 18);
        CToken token10 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 10", token10, CToken.TK_IDENT, "k3", 1, 19);
        CToken token11 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 11", token11, CToken.TK_RBRA, "]", 1, 21);
        CToken token12 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 12", token12, CToken.TK_NUM, "4", 1, 22);
        CToken token13 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 13", token13, CToken.TK_IDENT, "lm5n", 1, 23);
        CToken token14 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 14", token14, CToken.TK_EOF, "end_of_file", 1, 27);
    }

    @Test
    public void unsignedFactorIdentType() {
        inputStream.setInputString("i_a ip_a ia_a ipa_a");
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_IDENT, "i_a", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_IDENT, "ip_a", 1, 5);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_IDENT, "ia_a", 1, 10);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 4", token4, CToken.TK_IDENT, "ipa_a", 1, 15);
        CToken token5 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 5", token5, CToken.TK_EOF, "end_of_file", 1, 20);
    }
}
