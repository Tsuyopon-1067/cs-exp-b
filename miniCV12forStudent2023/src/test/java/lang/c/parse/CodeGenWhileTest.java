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

public class CodeGenWhileTest {

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
    public void codeGenWhileTest() throws FatalErrorException {
        inputStream.setInputString( "i_b = 1;" +
                                    "while (i_b == 1) { \r\n" +
                                    "   input i_a; \r\n" +
                                    "   i_b = 4; \r\n" +
                                    "}");
        String expected = """
                    . = 0x100
                    JMP    __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6     ; ProgramNode: 計算用スタック初期化
                    MOV    #i_b, (R6)+
                    MOV    #1, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
            WHILE1:
                    MOV    #i_b, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    #1, (R6)+
                    MOV    -(R6), R0
                    MOV    -(R6), R1
                    MOV    #0x0001, R2
                    CMP    R1, R0
                    BRZ    EQ2
                    CLR    R2
            EQ2:    MOV    R2, (R6)+
                    MOV    -(R6), R0
                    BRZ    WHILEEND1

                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    #0xFFE0, R1
                    MOV    (R1), (R0)

                    MOV    #i_b, (R6)+
                    MOV    #4, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)

                    JMP    WHILE1
            WHILEEND1:

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
    public void codeGenWhileNextTest() throws FatalErrorException {
        inputStream.setInputString("""
            while (true) {
                input i_a;
                while (false) {
                    output i_a;
                }
                i_a=4;
            }
                """);
        String expected = """
                    . = 0x100
                    JMP    __START ; ProgramNode: 最初の実行文へ
            __START:
                    MOV    #0x1000, R6     ; ProgramNode: 計算用スタック初期化

            WHILE1:
                    MOV    #0x0001, (R6)+
                    MOV    -(R6), R0
                    BRZ    WHILEEND1

                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    #0xFFE0, R1
                    MOV    (R1), (R0)
            WHILE2:
                    MOV    #0x0000, (R6)+
                    MOV    -(R6), R0
                    BRZ    WHILEEND2

                    MOV    #i_a, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    #0xFFE0, R0
                    MOV    -(R6), (R0)
                    JMP    WHILE2
            WHILEEND2:

                    MOV    #i_a, (R6)+
                    MOV    #4, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    JMP    WHILE1
            WHILEEND1:
                    HLT
                    .END
                """;

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
