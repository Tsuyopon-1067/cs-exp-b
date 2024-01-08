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

public class IsFirstTest_cv04 {
    // Test that each class's isFirst() is valid
    // Distant future, you should add necessary test cases to each Test code.

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
    public void testPrimary() throws FatalErrorException {
        String[] testDataArr = { "*A_BC12", "A_BC12"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, Primary.isFirst(firstToken), is(true));    
        }
    }

    @Test
    public void testPrimaryMult() throws FatalErrorException {
        String[] testDataArr = { "*A_BC12" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, PrimaryMult.isFirst(firstToken), is(true));    
        }
    }

    @Test
    public void testVariable() throws FatalErrorException {
        String[] testDataArr = { "Id3nt", "ident[12]" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, Variable.isFirst(firstToken), is(true));    
        }
    }

    @Test
    public void testArray() throws FatalErrorException {
        String[] testDataArr = { "[123]" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, Array.isFirst(firstToken), is(true));    
        }
    }
    
    @Test
    public void testAddressToValue() throws FatalErrorException {
        // AddressToValue ::= primary
        String[] testDataArr = { "hogeVal" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, AddressToValue.isFirst(firstToken), is(true));    
        }
    }
    
    @Test
    public void testFactorCV04() throws FatalErrorException {
        String[] testDataArr = {"13", "+13", "-13", "ident", "&", "(3+2)", "*variable"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, Factor.isFirst(firstToken), is(true));    
        }
    }

}
