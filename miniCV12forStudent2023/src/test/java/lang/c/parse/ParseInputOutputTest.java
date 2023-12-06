package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import lang.c.parse.statement.StatementInput;
import lang.c.parse.statement.StatementOutput;

public class ParseInputOutputTest {

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
    public void parseInputTestCrrect() throws FatalErrorException {
        String[] testDataArr = {
            "input i_a;",
            "input ip_a;",
            "input *ip_a;",
            "input ia_a[3];",
            "input ipa_a[3];",
            "input *ipa_a[3];",
            "input c_a;",
        };

        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            CParseRule cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Ignore
    @Test
    public void parseInputTestError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("input 3;", "inputの後ろはprimaryです"),
            new HelperTestStrMsg("input i_a+2;", "文末は;です"),
            new HelperTestStrMsg("input 3", "inputの後ろはprimaryです"),
            new HelperTestStrMsg("input ;", "inputの後ろはprimaryです"),
            new HelperTestStrMsg("input &i_a", "inputの後ろはprimaryです"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            CParseRule cp = new StatementInput(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData.getTestStr() + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }

    @Test
    public void parseOutputTestCrrect() throws FatalErrorException {
        String[] testDataArr = {
            "output i_a;",
            "output ip_a;",
            "output *ip_a;",
            "output ia_a[3];",
            "output ipa_a[3];",
            "output *ipa_a[3];",

            "output &i_a;",
            "output &ia_a[3];",

            "output 3;",
            "output &3;",

            "output i_a+3;",
            "output i_a+3*2;",

            "output c_a;",
        };

        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementOutput.isFirst(firstToken), is(true));
            CParseRule cp = new StatementOutput(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Ignore
    @Test
    public void parseOutputTestError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("output 3", "文末は;です"),
            new HelperTestStrMsg("output ;", "outputの後ろはexpressionです"),
            new HelperTestStrMsg("output &i_a", "文末は;です"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementOutput.isFirst(firstToken), is(true));
            CParseRule cp = new StatementOutput(cpContext);

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
