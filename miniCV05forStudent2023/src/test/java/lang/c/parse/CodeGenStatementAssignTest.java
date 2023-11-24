package lang.c.parse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.TestHelper;

public class CodeGenStatementAssignTest {

    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    CTokenizer tokenizer;
    IOContext context;
    CParseContext cpContext;
    TestHelper helper = new TestHelper();

    @Before
    public void setUp() {
        inputStream = new InputStreamForTest();
        outputStream = new PrintStreamForTest(System.out);
        errorOutputStream = new PrintStreamForTest(System.err);
        context = new IOContext(inputStream, outputStream, errorOutputStream);
        tokenizer = new CTokenizer(new CTokenRule());
        cpContext = new CParseContext(context, tokenizer);
    }

    @After
    public void tearDown() {
        inputStream = null;
        outputStream = null;
        errorOutputStream = null;
        tokenizer = null;
        context = null;
        cpContext = null;
    }

    @Test
    public void assign() throws FatalErrorException {
        inputStream.setInputString("i_A = i_B;");
        String expected[] = {
            "Write",
            "down",
            "the",
            "output",
            "you",
            "have",
            "decided",
            "on",
            "here"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (1) 整数型の扱い
    @Test
    public void assignInt() throws FatalErrorException {
        inputStream.setInputString("i_a=0;");
        String expected[] = {
            "Write",
            "down",
            "the",
            "output",
            "you",
            "have",
            "decided",
            "on",
            "here"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (2) ポインタ型の扱い
    // Please copy and paste the above and add the specified test case to the following

    // (3) 配列型の扱い
    @Test
    public void assignArray() throws FatalErrorException {
        inputStream.setInputString("ia_a[3]=1;");
        String expected[] = {
            "Write",
            "down",
            "the",
            "output",
            "you",
            "have",
            "decided",
            "on",
            "here"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (4) ポインタ配列型の扱い
    // Please copy and paste the above code and add the specified test case to the following

}
