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

public class CodeGenExpressionTest {
    // Test for Expression Node of "cv00".

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

    // Test for "cv00"
    @Test
    public void codeGenExpressionAdd2Term() throws FatalErrorException {
        inputStream.setInputString("7 + 2");
        String expected[] = {
            "	MOV	#7, (R6)+",
            "	MOV	#2, (R6)+",
            "	MOV	-(R6), R0",
            "	MOV	-(R6), R1",
            "	ADD	R1, R0",
            "	MOV	R0, (R6)+"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenExpressionAdd3Term() throws FatalErrorException {
        inputStream.setInputString("13 + 7 + 2");
        String expected[] = {
            "	MOV	#13, (R6)+",
            "	MOV	#7, (R6)+",
            "	MOV	-(R6), R0",
            "	MOV	-(R6), R1",
            "	ADD	R1, R0",
            "	MOV	R0, (R6)+",
            "	MOV	#2, (R6)+",
            "	MOV	-(R6), R0",
            "	MOV	-(R6), R1",
            "	ADD	R1, R0",
            "	MOV	R0, (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Expression(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
