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
import lang.c.parse.statement.StatementWhile;

public class ParseWhileTest {

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
    public void parseWhileTestCrrect() throws FatalErrorException {
        String[] testDataArr = {
            "while (true) { i_a=1; }",
            "while (true) { while (true) { i_a=1; i_b=2;} }",

            "while (true) i_a=1;",
            "while (true) while (true) while (true) i_a=1;"
        };

        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementWhile.isFirst(firstToken), is(true));
            CParseRule cp = new StatementWhile(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void parseWhileTestError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("while i_a==1", "whileの後ろはconditionBlockです"),
            new HelperTestStrMsg("while (true)", "whileブロックの中はstatementです"),

            new HelperTestStrMsg("while i_a==1", "whileの後ろはconditionBlockです"),
            new HelperTestStrMsg("while ( }", "(の後ろはbitExpressionです"),
            new HelperTestStrMsg("while (i_a==3", ")が閉じていません"),
            new HelperTestStrMsg("while (true) { i_a=1; aaa", "Primaryの後ろは=です"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementWhile.isFirst(firstToken), is(true));
            CParseRule cp = new StatementWhile(cpContext);

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
