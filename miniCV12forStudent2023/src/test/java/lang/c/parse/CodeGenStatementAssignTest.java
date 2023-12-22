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
import lang.c.parse.statement.StatementAssign;

public class CodeGenStatementAssignTest {

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
    public void assign() throws FatalErrorException {
        inputStream.setInputString("i_A = i_B;");
        String expected =
        """
        MOV     #i_A, (R6)+
        MOV     #i_B, (R6)+
        MOV     -(R6), R0
        MOV     (R0), (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (1) 整数型の扱い
    @Ignore
    @Test
    public void assignInt() throws FatalErrorException {
        inputStream.setInputString("i_a=0;");
        String expected =
        """
        MOV     #i_a, (R6)+
        MOV     #0, (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (2) ポインタ型の扱い
    // Please copy and paste the above and add the specified test case to the following
    @Ignore
    @Test
    public void assignPoint() throws FatalErrorException {
        inputStream.setInputString("ip_a=&1;");
        String expected =
        """
        MOV     #ip_a, (R6)+
        MOV     #1, (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Ignore
    @Test
    public void assignPointRef() throws FatalErrorException {
        inputStream.setInputString("*ip_a=1;");
        String expected =
        """
        MOV     #ip_a, (R6)+
        MOV     -(R6), R0
        MOV     (R0), (R6)+
        MOV     #1, (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Ignore
    @Test
    public void assignDoubleStatement() throws FatalErrorException {
        inputStream.setInputString("ip_a=&1;*ip_a=1;");
        String expected =
        """
        . = 0x100
        JMP	__START
        __START:
        MOV	#0x1000, R6
        MOV     #ip_a, (R6)+
        MOV     #1, (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        MOV     #ip_a, (R6)+
        MOV     -(R6), R0
        MOV     (R0), (R6)+
        MOV     #1, (R6)+
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

    // (3) 配列型の扱い
    @Ignore
    @Test
    public void assignArray() throws FatalErrorException {
        inputStream.setInputString("ia_a[3]=1;");
        String expected =
        """
        MOV     #ia_a, (R6)+
        MOV     #3, (R6)+
        MOV     -(R6), R0
        ADD     -(R6), R0
        MOV     R0, (R6)+
        MOV     #1, (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // (4) ポインタ配列型の扱い
    // Please copy and paste the above code and add the specified test case to the following
    @Ignore
    @Test
    public void assignPointArray() throws FatalErrorException {
        inputStream.setInputString("ipa_a[3]=&3;");
        String expected =
        """
        MOV     #ipa_a, (R6)+
        MOV     #3, (R6)+
        MOV     -(R6), R0
        ADD     -(R6), R0
        MOV     R0, (R6)+
        MOV     #3, (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Ignore
    @Test
    public void assignPointArrayMult() throws FatalErrorException {
        inputStream.setInputString("*ipa_a[3]=3;");
        String expected =
        """
        MOV     #ipa_a, (R6)+
        MOV     #3, (R6)+
        MOV     -(R6), R0
        ADD     -(R6), R0
        MOV     R0, (R6)+
        MOV     -(R6), R0
        MOV     (R0), (R6)+
        MOV     #3, (R6)+
        MOV     -(R6), R1
        MOV     -(R6), R0
        MOV     R1, (R0)
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new StatementAssign(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
