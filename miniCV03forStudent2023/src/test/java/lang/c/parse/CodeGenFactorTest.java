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

public class CodeGenFactorTest {

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
    public void codeGenFactor() throws FatalErrorException {
        inputStream.setInputString("2");  // Test for "2"
        String expected[] = {
            ";;; factor starts",
            ";;; number starts",
            "	MOV	#2, (R6)+	; Number: 数を積む[[1行目,1文字目の'2']]",
            ";;; number completes",
            ";;; factor completes"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Factor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

<<<<<<< HEAD
=======
    @Test
    public void codeGenFactorP2() throws FatalErrorException {
        inputStream.setInputString("+2");
        String expected[] = {
            "MOV #2, (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Factor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenFactorM2() throws FatalErrorException {
        inputStream.setInputString("-2");
        String expected[] = {
            "MOV #2, (R6)+",
            "MOV #0, R0",
            "SUB -(R6), R0",
            "MOV R0, (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Factor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenFactorParen() throws FatalErrorException {
        inputStream.setInputString("(2+3)");
        String expected[] = {
            "MOV #2, (R6)+",
            "MOV #3, (R6)+",
            "MOV -(R6), R0",
            "MOV -(R6), R1",
            "ADD R1, R0",
            "MOV R0, (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Factor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

>>>>>>> origin/cv04
}