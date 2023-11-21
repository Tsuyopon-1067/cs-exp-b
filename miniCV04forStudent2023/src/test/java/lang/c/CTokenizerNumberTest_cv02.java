package lang.c;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;

public class CTokenizerNumberTest_cv02 {

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

    // Test for Octal and Hex Number of "cv02"

    // 16進数

    @Test
    public void hex() {
        String testString = "0xffe0";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "0xffe0", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 7);
    }

    @Test
    public void hexNumber0x0() {
        String testString = "0x0";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "0x0", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 4);
    }

    @Test
    public void hexNumberMax() {
        String testString = "0xffff";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "0xffff", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 7);
    }

    // Please copy and paste the above code and add the specified test case to the following
    // ここに追加するテストケース："0xfffff", "0xffgf"
    @Test
    public void hexTooLargeBit1() {
        String testString = "0xfffff";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_ILL, "0xfffff", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 8);
    }

    @Test
    public void hexTooLargeBit2() {
        String testString = "0xffgf";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "0xff", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_IDENT, "gf", 1, 5);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token4, CToken.TK_EOF, "end_of_file", 1, 7);
    }

    @Test
    public void hexNumberError0x() {
        String testString = "0x";
        inputStream.setInputString(testString);
        CToken token = tokenizer.getNextToken(cpContext);
        helper.checkToken("token ", token, CToken.TK_ILL, "0x", 1, 1);
    }

    // 8進数
    @Test
    public void octal() {
        String testString = "0472";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "0472", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 5);
    }

    @Test
    public void octalZero() {
        String testString = "0";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "0", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 2);
    }

    @Test
    public void octalNumber() {
        String testString = "0177777";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "0177777", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 8);
    }

    // Please copy and paste the above code and add the specified test case to the following
    // ここに追加するテストケース："0277777", "01786"
    @Test
    public void octalTooLargeNumber() {
        String testString = "0277777";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_ILL, "0277777", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 8);
    }

    @Test
    public void octalIllNumber() {
        String testString = "01786";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "017", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_NUM, "86", 1, 4);
        CToken token3 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token3, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    // 10進数
    // ここに追加するテストケース："32767", "32768", "123a4"
    @Test
    public void decNumberMax() {
        String testString = "32767";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "32767", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void decTooLargeNumber() {
        String testString = "32769";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_ILL, "32769", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_EOF, "end_of_file", 1, 6);
    }

    @Test
    public void decIllNumber() {
        String testString = "123a4";
        inputStream.setInputString(testString);
        CToken token1 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 1", token1, CToken.TK_NUM, "123", 1, 1);
        CToken token2 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 2", token2, CToken.TK_IDENT, "a4", 1, 4);
        CToken token4 = tokenizer.getNextToken(cpContext);
        helper.checkToken("token 3", token4, CToken.TK_EOF, "end_of_file", 1, 6);
    }
}
