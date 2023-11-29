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
        String expected = "MOV\t#0x0001, (R6)+";

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionFALSE() throws FatalErrorException {
        inputStream.setInputString("false");
        String expected = "MOV\t#0x0000, (R6)+";

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionLT() throws FatalErrorException {
        inputStream.setInputString("1 < 2");
        String expected = """
            MOV    #1, (R6)+;
            MOV    #2, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            CMP    R0, R1;
            BRN    LT1;
            CLR    R2;
            LT1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionLE() throws FatalErrorException {
        inputStream.setInputString("1 <= 2");
        String expected = """
            MOV    #1, (R6)+;
            MOV    #2, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            ADD    #1, R1;
            CMP    R0, R1;
            BRN    LE1;
            CLR    R2;
            LE1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionGT() throws FatalErrorException {
        inputStream.setInputString("1 > 2");
        String expected = """
            MOV    #1, (R6)+;
            MOV    #2, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            CMP    R1, R0;
            BRN    GT1;
            CLR    R2;
            GT1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionGE() throws FatalErrorException {
        inputStream.setInputString("1 >= 2");
        String expected = """
            MOV    #1, (R6)+;
            MOV    #2, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            ADD    #1, R0;
            CMP    R1, R0;
            BRN    GE1;
            CLR    R2;
            GE1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionEQ() throws FatalErrorException {
        inputStream.setInputString("1 == 2");
        String expected = """
            MOV    #1, (R6)+;
            MOV    #2, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            CMP    R1, R0;
            BRZ    EQ1;
            CLR    R2;
            EQ1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionNE() throws FatalErrorException {
        inputStream.setInputString("1 != 2");
        String expected = """
            MOV    #1, (R6)+;
            MOV    #2, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            CLR    R2;
            CMP    R1, R0;
            BRZ    NE1;
            MOV    #0x0001, R2;
            NE1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionLT2() throws FatalErrorException {
        inputStream.setInputString("i_a < 3");
        String expected = """
            MOV    #i_a, (R6)+;
            MOV    -(R6), R0;
            MOV    (R0), (R6)+
            MOV    #3, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            CMP    R0, R1;
            BRN    LT1;
            CLR    R2;
            LT1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionGT2() throws FatalErrorException {
        inputStream.setInputString("10 > *ip_a");
        String expected = """
            MOV    #10, (R6)+;
            MOV    #ip_a, (R6)+;
            MOV    -(R6), R0;
            MOV    (R0), (R6)+;
            MOV    -(R6), R0;
            MOV    (R0), (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            CMP    R1, R0;
            BRN    GT1;
            CLR    R2;
            GT1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void conditionEQ2() throws FatalErrorException {
        inputStream.setInputString("ia_a[1] == 4");
        String expected = """
            MOV    #ia_a, (R6)+;
            MOV    #1, (R6)+;
            MOV    -(R6), R0;
            ADD    -(R6), R0;
            MOV    R0, (R6)+;
            MOV    -(R6), R0;
            MOV    (R0), (R6)+;
            MOV    #4, (R6)+;
            MOV    -(R6), R0;
            MOV    -(R6), R1;
            MOV    #0x0001, R2;
            CMP    R1, R0;
            BRZ    EQ1;
            CLR    R2;
            EQ1: MOV R2, (R6)+;
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Condition(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
