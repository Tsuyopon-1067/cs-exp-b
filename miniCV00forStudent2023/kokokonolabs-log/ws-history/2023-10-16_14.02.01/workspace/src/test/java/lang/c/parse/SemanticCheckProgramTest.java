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
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckProgramTest {

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

    @Test   // Program始点のテストSemanticCheck
    public void noResponseDoBeforeParse() throws FatalErrorException {
        inputStream.setInputString("13 + 2");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Expression.isFirst(firstToken), is(true));
        CParseRule cp = new Program(cpContext);

        cp.semanticCheck(cpContext);        
        String errorOutput = errorOutputStream.getPrintBufferString();
        assertThat(errorOutput, is(""));
    }
    
    @Test   // Expression始点のSemanticCheck
    public void ExpressionAddNoError() throws FatalErrorException {
        inputStream.setInputString("13 + 2");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Expression.isFirst(firstToken), is(true));
        Expression cp = new Expression(cpContext);
        cp.parse(cpContext);

        cp.semanticCheck(cpContext);
        String errorOutput = errorOutputStream.getPrintBufferString();
        assertThat(errorOutput, is(""));
    }

}
