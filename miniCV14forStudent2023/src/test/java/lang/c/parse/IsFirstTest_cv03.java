package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

public class IsFirstTest_cv03 {
    // Test that each class's isFirst() is valid

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
    public void testPlusFactor() throws FatalErrorException {
        String[] testDataArr = { "+13" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, PlusFactor.isFirst(firstToken), is(true));    
        }
    }

    @Test
    public void testMinusFactor() throws FatalErrorException {
        String[] testDataArr = { "-13" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, MinusFactor.isFirst(firstToken), is(true));    
        }
    }
    
    // 2回 getNextToken をして，焦点が 記号 に合うようにしてある
    @Test
    public void testTermMult() throws FatalErrorException {
        String[] testDataArr = { "2*3" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, TermMult.isFirst(firstToken), is(true));    
        }
    }

    @Test
    public void testTermDiv() throws FatalErrorException {
        String[] testDataArr = { "2/3" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, TermDiv.isFirst(firstToken), is(true));    
        }
    }

    @Test
    public void testTermCV03() throws FatalErrorException {
        String[] testDataArr = { "1", "+1", "-1", "(1)", "&" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, Term.isFirst(firstToken), is(true));    
        }
    }
}
