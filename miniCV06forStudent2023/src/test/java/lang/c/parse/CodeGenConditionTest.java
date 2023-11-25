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

public class CodeGenConditionTest {
    // Test for Conditions of "true, false, LT, LE, GT, GE, EQ, NE".

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

    @Test
    public void conditionTRUE() throws FatalErrorException {
        inputStream.setInputString("true");
        String expected[] = {
            "Write down the output you have decided on here",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionFALSE() throws FatalErrorException {
        inputStream.setInputString("false");
        String expected[] = {
            "Write down the output you have decided on here",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionLT() throws FatalErrorException {
        inputStream.setInputString("1<2");
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
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following

}
