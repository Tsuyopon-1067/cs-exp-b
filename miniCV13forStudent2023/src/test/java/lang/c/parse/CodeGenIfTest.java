package lang.c.parse;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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

public class CodeGenIfTest {

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

    @Ignore
    @Test
    public void codeGenIfTest() throws FatalErrorException {
        inputStream.setInputString( "if (false) {\n" +
                                        "i_a=3;\n" +
                                    "}");
        String expected = """
            ; program starts
                    . = 0x100
                    JMP     __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6
                    MOV    #0x0000, (R6)+
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

    // Please copy and paste the above code and add the specified test case to the following

    @Ignore
    @Test
    public void codeGenIfElseTest() throws FatalErrorException {
        inputStream.setInputString("""
            if (true) {
                i_a=1;
            } else {
                i_a=2;
            }
        """);
        String expected = """
            ; program starts
                    . = 0x100
                    JMP    __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6
                    MOV    #0x0001, (R6)+
                    MOV    -(R6), R0
                    BRZ    ELSE1
                    MOV    #i_a, (R6)+
                    MOV    #1, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    ENDIF1
            ELSE1:
                    MOV    #i_a, (R6)+
                    MOV    #2, (R6)+
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

    @Ignore
    @Test
    public void codeGenIfElseIfTest() throws FatalErrorException {
        inputStream.setInputString("""
            if (i_a == 3) {
                i_a=0;
            } else if (i_a == 4){
                i_a=1;
            } else {
                i_a=2;
            }
        """);
        String expected = """
            ; program starts
                    . = 0x100
                    JMP    __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6

                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R0
                    MOV    -(R6), R1
                    MOV    #0x0001, R2
                    CMP    R1, R0
                    BRZ    EQ2
                    CLR    R2
            EQ2:    MOV    R2, (R6)+

                    MOV    -(R6), R0
                    BRZ    ELSE1

                    MOV    #i_a, (R6)+
                    MOV    #0, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    ENDIF1
            ELSE1:
                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    #4, (R6)+
                    MOV    -(R6), R0
                    MOV    -(R6), R1
                    MOV    #0x0001, R2
                    CMP    R1, R0
                    BRZ    EQ4
                    CLR    R2
            EQ4:    MOV    R2, (R6)+

                    MOV    -(R6), R0
                    BRZ    ELSE3

                    MOV    #i_a, (R6)+
                    MOV    #1, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    ENDIF3
            ELSE3:
                    MOV    #i_a, (R6)+
                    MOV    #2, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
            ENDIF3:
            ENDIF1:
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Ignore
    @Test
    public void codeGenIfElseIfElseIfTest() throws FatalErrorException {
        inputStream.setInputString("""
            i_a = 54;
            if (i_a == 3) {
                i_a=0;
            } else if (i_a == 4){
                i_a=1;
            } else if (i_a ==54){
                i_a=2;
            } else {
                i_a=3;
            }
        """);
        String expected = """
            ; program starts
                    . = 0x100
                    JMP    __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6

                    MOV    #i_a, (R6)+
                    MOV    #54, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)

                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R0
                    MOV    -(R6), R1
                    MOV    #0x0001, R2
                    CMP    R1, R0
                    BRZ    EQ2
                    CLR    R2
            EQ2:    MOV    R2, (R6)+

                    MOV    -(R6), R0
                    BRZ    ELSE1

                    MOV    #i_a, (R6)+
                    MOV    #0, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    ENDIF1
            ELSE1:
                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    #4, (R6)+
                    MOV    -(R6), R0
                    MOV    -(R6), R1
                    MOV    #0x0001, R2
                    CMP    R1, R0
                    BRZ    EQ4
                    CLR    R2
            EQ4:    MOV    R2, (R6)+

                    MOV    -(R6), R0
                    BRZ    ELSE3

                    MOV    #i_a, (R6)+
                    MOV    #1, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    ENDIF3
            ELSE3:
                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    #54, (R6)+
                    MOV    -(R6), R0
                    MOV    -(R6), R1
                    MOV    #0x0001, R2
                    CMP    R1, R0
                    BRZ    EQ6
                    CLR    R2
            EQ6:    MOV    R2, (R6)+

                    MOV    -(R6), R0
                    BRZ    ELSE5

                    MOV    #i_a, (R6)+
                    MOV    #2, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    ENDIF5
            ELSE5:
                    MOV    #i_a, (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
            ENDIF5:
            ENDIF3:
            ENDIF1:
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Ignore
    @Test
    public void codeGenIfOneLineTest() throws FatalErrorException {
        inputStream.setInputString("""
            if (true) i_a=1;
            else i_b=1;
        """);
        String expected = """
            ; program starts
                    . = 0x100
                    JMP     __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6
                    MOV    #0x0001, (R6)+
                    MOV    -(R6), R0
                    BRZ    ELSE1

                    MOV    #i_a, (R6)+
                    MOV    #1, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    ENDIF1
            ELSE1:
                    MOV    #i_b, (R6)+
                    MOV    #1, (R6)+
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

    @Ignore
    @Test
    public void codeGenIf3OneLineTest() throws FatalErrorException {
        inputStream.setInputString("""
            if (true) if (false) if (true) i_a=1;
        """);
        String expected = """
            ; program starts
                    . = 0x100
                    JMP    __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6

                    MOV    #0x0001, (R6)+
                    MOV    -(R6), R0
                    BRZ    ENDIF1

                    MOV    #0x0000, (R6)+
                    MOV    -(R6), R0
                    BRZ    ENDIF2

                    MOV    #0x0001, (R6)+
                    MOV    -(R6), R0
                    BRZ    ENDIF3

                    MOV    #i_a, (R6)+
                    MOV    #1, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)

            ENDIF3:
            ENDIF2:
            ENDIF1:
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}