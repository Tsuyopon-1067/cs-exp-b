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
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;

public class ParseFactorTest {
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

    // 実験5以降は Program が true ではなくなるのでこのメソッドに @Ignore をつけてください
    @Test
    public void parseRCURWithoutLCUR()  {
        String[] testDataArr = {"(1+3))"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            Program cp = new Program(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(""));
            }
        }
    }

    @Test
    public void parseNotCloseRPAR() {
        String[] testDataArr = {"((1+3)"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, UnsignedFactor.isFirst(firstToken), is(true));
            UnsignedFactor cp = new UnsignedFactor(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("弧が閉じられていません"));
            }
        }
    }

    @Test
    public void parseOnlyLPAR() {
        String[] testDataArr = {"("};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, UnsignedFactor.isFirst(firstToken), is(true));
            UnsignedFactor cp = new UnsignedFactor(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("左括弧の後ろはExpressionです"));
            }
        }
    }

    @Test
    public void parseOnlyMinusFactor() {
        String[] testDataArr = {"-"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Factor.isFirst(firstToken), is(true));
            Factor cp = new Factor(cpContext);

            try {
                cp.parse(cpContext);
                fail("Error should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString("-の後はUnsignedFactorです"));
}
