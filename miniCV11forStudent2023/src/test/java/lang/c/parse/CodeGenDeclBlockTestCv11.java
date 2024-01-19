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

public class CodeGenDeclBlockTestCv11 {

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
    public void codeGenDeclBlockTest() throws FatalErrorException {
        inputStream.setInputString("""
            int a;
            {
            int a;
            int b;
            int c[5];
            int d;

            a = 1;
            b = 2;
            c[2] = 3;
            d = 4;
            }
            {
            int c;
            int d;

            c = 5;
            d = 6;
            }
                """);
        String expected = """
                    . = 0x100
                    JMP     __START
            a:      .BLKW 1
            __START:
                    MOV     #0x1000, R6
                    MOV     R4, (R6)+
                    MOV     R6, R4
                    ADD     #8, R6
                    MOV     #0, R0
                    ADD     R4, R0
                    MOV     R0, (R6)+
                    MOV     #1, (R6)+
                    MOV     -(R6), R1
                    MOV     -(R6), R0
                    MOV     R1, (R0)
                    MOV     #1, R0
                    ADD     R4, R0
                    MOV     R0, (R6)+
                    MOV     #2, (R6)+
                    MOV     -(R6), R1
                    MOV     -(R6), R0
                    MOV     R1, (R0)
                    MOV     #2, R0
                    ADD     R4, R0
                    MOV     R0, (R6)+
                    MOV     #2, (R6)+
                    MOV     -(R6), R0
                    ADD     -(R6), R0
                    MOV     R0, (R6)+
                    MOV     #3, (R6)+
                    MOV     -(R6), R1
                    MOV     -(R6), R0
                    MOV     R1, (R0)
                    MOV     #7, R0
                    ADD     R4, R0
                    MOV     R0, (R6)+
                    MOV     #4, (R6)+
                    MOV     -(R6), R1
                    MOV     -(R6), R0
                    MOV     R1, (R0)
                    MOV     R4, R6
                    MOV     -(R6), R4
                    MOV     R4, (R6)+
                    MOV     R6, R4
                    ADD     #2, R6
                    MOV     #0, R0
                    ADD     R4, R0
                    MOV     R0, (R6)+
                    MOV     #5, (R6)+
                    MOV     -(R6), R1
                    MOV     -(R6), R0
                    MOV     R1, (R0)
                    MOV     #1, R0
                    ADD     R4, R0
                    MOV     R0, (R6)+
                    MOV     #6, (R6)+
                    MOV     -(R6), R1
                    MOV     -(R6), R0
                    MOV     R1, (R0)
                    MOV     R4, R6
                    MOV     -(R6), R4
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}