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

public class CodeGenExpressionTest_cv03 {
    // Test for Expression Node of "cv03".

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

    // Test for Four arithmetic operations and Priority with parenthesis and Sign ±.
    // Test for Priority w/ PAR in "cv03"
    @Test
    public void codeGenTermMultWithParentheses() throws FatalErrorException {
        inputStream.setInputString("(1+2)*3");
        String expected[] = {
            "MOV #1, (R6)+",
            "MOV #2, (R6)+",
            "MOV -(R6), R0",
            "MOV -(R6), R1",
            "ADD R1, R0",
            "MOV R0, (R6)+",
            "MOV #3, (R6)+",
            "JSR MUL",
            "SUB #2, R6",
            "MOV R0, (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenTermMultPriority() throws FatalErrorException {
        inputStream.setInputString("1+2*3");
        String expected[] = {
            "MOV #1, (R6)+",
            "MOV #2, (R6)+",
            "MOV #3, (R6)+",
            "JSR MUL",
            "SUB #2, R6",
            "MOV R0, (R6)+",
            "MOV -(R6), R0",
            "MOV -(R6), R1",
            "ADD R1, R0",
            "MOV R0, (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenTermDivWithParenthesis() throws FatalErrorException {
        inputStream.setInputString("1/(2-3)");
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
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenTermDivMinus() throws FatalErrorException {
        inputStream.setInputString("1/2-3");
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
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenTermAllTest() throws FatalErrorException {
        inputStream.setInputString("(1+2)*3/-(4-5)");
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
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenTermSignTest() throws FatalErrorException {
        inputStream.setInputString("+4--5++2");
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
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
