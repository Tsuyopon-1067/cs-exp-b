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

public class CodeGenDeclarationTestCv10 {

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
    public void codeGenAndTest1() throws FatalErrorException {
        inputStream.setInputString("""
            int a, *b, c[10], *d[10];
            const int e=10, *f=&30;
            a = 10;
            *b = 3;
            b = &a;
            *b = 4;
            c[9]=10;
            *d[0]=10;
                """);
        String expected = """
            . = 0x100

            JMP     __START
            a:      .BLKW 1
            b:      .BLKW 1
            c:      .BLKW 10
            d:      .BLKW 10
            e:      .WORD 10
            f:      .WORD 30
            __START:

            MOV     #0x1000, R6
            MOV     #a, (R6)+
            MOV     #10, (R6)+
            MOV     -(R6), R1
            MOV     -(R6), R0
            MOV     R1, (R0)
            MOV     #b, (R6)+
            MOV     -(R6), R0
            MOV     (R0), (R6)+
            MOV     #3, (R6)+
            MOV     -(R6), R1
            MOV     -(R6), R0
            MOV     R1, (R0)
            MOV     #b, (R6)+
            MOV     #a, (R6)+
            MOV     -(R6), R1
            MOV     -(R6), R0
            MOV     R1, (R0)
            MOV     #b, (R6)+
            MOV     -(R6), R0
            MOV     (R0), (R6)+
            MOV     #4, (R6)+
            MOV     -(R6), R1
            MOV     -(R6), R0
            MOV     R1, (R0)
            MOV     #c, (R6)+
            MOV     #9, (R6)+
            MOV     -(R6), R0
            ADD     -(R6), R0
            MOV     R0, (R6)+
            MOV     #10, (R6)+
            MOV     -(R6), R1
            MOV     -(R6), R0
            MOV     R1, (R0)
            MOV     #d, (R6)+
            MOV     #0, (R6)+
            MOV     -(R6), R0
            ADD     -(R6), R0
            MOV     R0, (R6)+
            MOV     -(R6), R0
            MOV     (R0), (R6)+
            MOV     #10, (R6)+
            MOV     -(R6), R1
            MOV     -(R6), R0
            MOV     R1, (R0)
            HLT
            .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

@Ignore
@Test
    public void codeGenAndTest2() throws FatalErrorException {
        inputStream.setInputString("""
            int a, b, *c, d;
            const int e = 1, f = 2;
            int g[10];

            a=3;
            g[2]=a;
                """);
        String expected = """
            ; program starts
                    . = 0x100
                    JMP     __START ; ProgramNode: 最初の実行文へ
                    a:     .BLKW 1
                    b:     .BLKW 1
                    c:     .BLKW 1
                    d:     .BLKW 1
                    e:     .WORD 1
                    f:     .WORD 2
                    g:     .BLKW 10
            __START:
                    MOV    #0x1000, R6
                    MOV    #a, (R6)+
                    MOV    #3, (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    MOV    #g, (R6)+
                    MOV    #2, (R6)+
                    MOV    -(R6), R0
                    ADD    -(R6), R0
                    MOV    R0, (R6)+
                    MOV    #a, (R6)+
                    MOV    -(R6), R0
                    MOV    (R0), (R6)+
                    MOV    -(R6), R1
                    MOV    -(R6), R0
                    MOV    R1, (R0)
                    HLT
                    .END
                """;
        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}