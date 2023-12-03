package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.parse.statement.StatementIf;

public class ParseIfTest {

    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    CTokenizer tokenizer;
    IOContext context;
    CParseContext cpContext;

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

    void resetEnvironment() {
        tearDown();
        setUp();
    }

    @Test
    public void parseIfTestCrrect() throws FatalErrorException {
        String[] testDataArr = {
            "if (true) { i_a=1; i_b=2; }",
            "if (true) { i_a=1; i_b=2; } else { i_a=2; i_b=3;}",
            "if (true) { i_a=1; i_b=2; } else if ( true ) { i_a=2; } else { i_a=3; }",
            "if (true) { if (true) { if (true) { i_a=1; i_b=2; }}}",
            "if (true) i_a=1;",
            "if (true) i_a=1; else i_a=2;",
            "if (true) if (true) if (true) i_a=3;",

            "if (true) { i_a=1; i_b=2; }",
            "if (true) { i_a=1; i_b=2; } else { i_a=2; i_b=3;}",
            "if (true) { i_a=1; i_b=2; } else if ( true ) { i_a=2; } else { i_a=3; }",
            "if (true) { if (true) { if (true) { i_a=1; i_b=2; }}}",

            "if (true) i_a=1;",
            "if (true) i_a=1; else i_a=1;"
        };

        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            CParseRule cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void parseIfTestError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("if i_a==1", "ifの後ろはconditionBlockです"),
            new HelperTestStrMsg("if (true)", "ifブロックの中はstatementです"),
            new HelperTestStrMsg("if (true) i_a=1; else", "elseブロックの中はstatementです"),

            new HelperTestStrMsg("if i_a==1", "ifの後ろはconditionBlockです"),
            new HelperTestStrMsg("if ( }", "(の後ろはbitExpressionです"),
            new HelperTestStrMsg("if (i_a==3", ")が閉じていません"),
            new HelperTestStrMsg("if (true) { i_a=1; aaa", "Primaryの後ろは=です"),
            new HelperTestStrMsg("if (true) { i_a=1; i_b=2;} else bbb;", "Primaryの後ろは=です"),
            new HelperTestStrMsg("if (true) { i_a=1;} else { i_c=3;", "statmentの後ろは}です"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementIf.isFirst(firstToken), is(true));
            CParseRule cp = new StatementIf(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData.getTestStr() + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }
}
