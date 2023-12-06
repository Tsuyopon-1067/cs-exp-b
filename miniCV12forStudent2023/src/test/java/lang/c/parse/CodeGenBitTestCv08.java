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

public class CodeGenBitTestCv08 {

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
    public void codeGenAndTest() throws FatalErrorException {
        inputStream.setInputString( "if (true && false) {\n" +
                                        "i_a=3;\n" +
                                    "}");
        String expected = """
            ; program starts
                    . = 0x100
                    JMP     __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6
                    MOV    #0x0001, (R6)+
                    MOV    #0x0000, (R6)+
                    MOV    -(R6), R0
                    AND    -(R6), R0
                    MOV    R0, (R6)+
                    MOV    -(R6), R0
                    BRZ    ENDIF1
                    MOV    #i_a, (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
            ENDIF1:
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenOrTest() throws FatalErrorException {
        inputStream.setInputString( "if (true || false) {\n" +
                                        "i_a=3;\n" +
                                    "}");
        String expected = """
            ; program starts
                    . = 0x100
                    JMP     __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6
                    MOV    #0x0001, (R6)+
                    MOV    #0x0000, (R6)+
                    MOV    -(R6), R0
                    OR    -(R6), R0
                    MOV    R0, (R6)+
                    MOV    -(R6), R0
                    BRZ    ENDIF1
                    MOV    #i_a, (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
            ENDIF1:
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenNotTest() throws FatalErrorException {
        inputStream.setInputString( "if (!true) {\n" +
                                        "i_a=3;\n" +
                                    "}");
        String expected = """
            ; program starts
                    . = 0x100
                    JMP     __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6
                    MOV    #0x0001, (R6)+
                    MOV    -(R6), R0
                    XOR    #0x0001, R0
                    MOV    R0, (R6)+
                    MOV    -(R6), R0
                    BRZ    ENDIF1
                    MOV    #i_a, (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
            ENDIF1:
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenMixTest() throws FatalErrorException {
        inputStream.setInputString( "if (!<!true || false> && true) {\n" +
                                        "i_a=3;\n" +
                                    "}");
        String expected = """
            ; program starts
                    . = 0x100
                    JMP     __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6
                    MOV    #0x0001, (R6)+
                    MOV    -(R6), R0
                    XOR    #0x0001, R0
                    MOV    R0, (R6)+
                    MOV    #0x0000, (R6)+
                    MOV     -(R6), R0
                    OR     -(R6), R0
                    MOV    R0, (R6)+
                    MOV    -(R6), R0
                    XOR    #0x0001, R0
                    MOV    R0, (R6)+
                    MOV    #0x0001, (R6)+
                    MOV     -(R6), R0
                    AND     -(R6), R0
                    MOV    R0, (R6)+
                    MOV     -(R6), R0
                    BRZ    ENDIF1
                    MOV    #i_a, (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
            ENDIF1:
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

}