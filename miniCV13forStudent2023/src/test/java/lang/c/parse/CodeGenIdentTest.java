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

public class CodeGenIdentTest {
    // Test for UnsignedFactor node of "cv04".
    // i_a &i_a ip_a *ip_a ia_a[3] &ia_a[3] ipa_a[3] *ipa_a[3]

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
    @Ignore
    public void codeGenInt() throws FatalErrorException {
        inputStream.setInputString("i_a");
        String expected[] = {
            "	MOV #i_a, (R6)+",
            "	MOV\t-(R6), R0",
            "	MOV\t(R0), (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    @Ignore
    public void codeGenAMPInt() throws FatalErrorException {
        inputStream.setInputString("&i_a");
        String expected[] = {
            "	MOV #i_a, (R6)+",
        };

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    @Ignore
    public void codeGenPint() throws FatalErrorException {
        inputStream.setInputString("ip_a");
        String expected =
        """
        MOV #ip_a, (R6)+
        MOV -(R6), R0
        MOV (R0), (R6)+
        """;

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    @Ignore
    public void codeGenMultPint() throws FatalErrorException {
        inputStream.setInputString("*ip_a");
        String expected =
            """
            MOV #ip_a, (R6)+
            MOV -(R6), R0
            MOV (R0), (R6)+
            MOV -(R6), R0
            MOV (R0), (R6)+
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    @Ignore
    public void codeGenArray() throws FatalErrorException {
        inputStream.setInputString("ia_a[3]");
        String expected =
            """
            MOV #ia_a, (R6)+
            MOV #3, (R6)+
            MOV -(R6), R0
            ADD -(R6), R0
            MOV R0, (R6)+
            MOV -(R6), R0
            MOV (R0), (R6)+
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    @Test
    @Ignore
    public void codeGenArrayAmp() throws FatalErrorException {
        inputStream.setInputString("&ia_a[3]");
        String expected =
            """
            MOV #ia_a, (R6)+
            MOV #3, (R6)+
            MOV -(R6), R0
            ADD -(R6), R0
            MOV R0, (R6)+
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    @Ignore
    public void codeGenArrayPoint() throws FatalErrorException {
        inputStream.setInputString("ipa_a[3]");
        String expected =
            """
            MOV #ipa_a, (R6)+
            MOV #3, (R6)+
            MOV -(R6), R0
            ADD -(R6), R0
            MOV R0, (R6)+
            MOV -(R6), R0
            MOV (R0), (R6)+
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    // Please copy and paste the above code and add the specified test case to the following
    @Test
    @Ignore
    public void codeGenArrayPointAmp() throws FatalErrorException {
        inputStream.setInputString("*ipa_a[3]");
        String expected =
            """
            MOV #ipa_a, (R6)+
            MOV #3, (R6)+
            MOV -(R6), R0
            ADD -(R6), R0
            MOV R0, (R6)+
            MOV -(R6), R0
            MOV (R0), (R6)+
            MOV -(R6), R0
            MOV (R0), (R6)+
            """;

        // Check only code portion, not validate comments
        CParseRule rule = new UnsignedFactor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}