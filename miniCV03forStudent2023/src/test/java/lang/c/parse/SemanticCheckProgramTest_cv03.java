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
import lang.c.CType;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckProgramTest_cv03 {

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
    public void FactorWithMinusSignOverflow() throws FatalErrorException {
        String[] testDataArr = {"-36769"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Factor.isFirst(firstToken), is(true));
            Factor cp = new Factor(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData);
            } catch ( FatalErrorException e ) {
<<<<<<< HEAD
                assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
=======
                assertThat(e.getMessage(), containsString("-の後はUnsignedFactorです")); // オーバーフローで32769は数値だと認識しない
>>>>>>> origin/cv04
            }
        }
    }

    @Test
    public void FactorWithSignNotOverflow() throws FatalErrorException {
        String[] testDataArr = {"32767", "-32768"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Factor.isFirst(firstToken), is(true));
            Factor cp = new Factor(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            String errorOutput = errorOutputStream.getPrintBufferString();
            assertThat(errorOutput, is(""));

        }
    }

    @Test
    public void TermTypeOperationNoError() throws FatalErrorException {
        String[] testDataArr = {"1*1", "1/1"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            String errorOutput = errorOutputStream.getPrintBufferString();
            assertThat(errorOutput, is(""));
        }
    }
}
