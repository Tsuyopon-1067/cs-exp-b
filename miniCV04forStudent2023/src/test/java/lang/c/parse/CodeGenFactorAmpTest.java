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

public class CodeGenFactorAmpTest {
    // Test for "AMP" by FactorAMP node of "cv02".

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
    public void codeGenFactorAmp() throws FatalErrorException {
        inputStream.setInputString("&2");  // Test for "2"
        String expected[] = {
            "MOV	#2, (R6)+"
            };

        // Check only code portion, not validate comments
        FactorAmp rule = new FactorAmp(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenFactorWithAmp() throws FatalErrorException {
        inputStream.setInputString("&2");  // Test for "2"
        String expected[] = {
            "MOV	#2, (R6)+"
            };

        // Check only code portion, not validate comments
        CParseRule rule = new Factor(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
}
