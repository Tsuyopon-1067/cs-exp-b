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

public class ParseUnsignedFactorTest {
    // Test for UnsignedFactor node, added in "cv04"
      
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
    public void parsePrimaryMultVariableTest() throws FatalErrorException {
        inputStream.setInputString("*12");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Primary.isFirst(firstToken), is(true));
        Primary ruleNumber = new Primary(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
            fail("FatalErrorException should be invoked");
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
        }
    }

    @Test
    public void parseArrayMissingRBRA() throws FatalErrorException {
        inputStream.setInputString("i_a[i_a[3]");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Primary.isFirst(firstToken), is(true));
        Primary ruleNumber = new Primary(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
            fail("FatalErrorException should be invoked");
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
        }
    }

    @Test
    public void parseExpressionMissingRPAR() throws FatalErrorException {
        inputStream.setInputString("(1+(3+4)");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Expression.isFirst(firstToken), is(true));
        Expression ruleNumber = new Expression(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
            fail("FatalErrorException should be invoked");
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
        }
    }

    // このテストは実験5で Program が isFirst ではなくなることに注意
    @Test
    public void parseArrayOnlyIdent() throws FatalErrorException {
        inputStream.setInputString("12[4]");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Program.isFirst(firstToken), is(true));
        Program ruleNumber = new Program(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
            fail("FatalErrorException should be invoked");
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
        }
    }

    @Test
    public void parseVariableNotFulfillNotation() throws FatalErrorException {
        inputStream.setInputString("i+a");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Primary.isFirst(firstToken), is(true));
        Primary ruleNumber = new Primary(cpContext);
        CParseRule rule = ruleNumber;
        try {
            rule.parse(cpContext);
            rule.semanticCheck(cpContext);  // Error here.
            fail("FatalErrorException should be invoked");
        } catch ( FatalErrorException e ) {
            assertThat(e.getMessage(), containsString("Write down the output you have decided on here"));
        }
    }
}
