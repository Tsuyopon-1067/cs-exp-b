package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.containsString;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;

public class ParseProgramTest {

    InputStreamForTest inputStream;
    PrintStreamForTest outputStream;
    PrintStreamForTest errorOutputStream;
    CTokenizer tokenizer;
    IOContext context;
    CParseContext cpContext;

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
    public void parseErrorEndOfProgram() throws FatalErrorException {
        inputStream.setInputString("13 + 7@");
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Program.isFirst(firstToken), is(true));
        CParseRule cpProgram = new Program(cpContext);
        
        try {
            // call test target
            cpProgram.parse(cpContext);
            fail("FatalErrorException should be invoked.");
        } catch ( FatalErrorException e ) {
            String errorMessage = e.getMessage();
            assertThat(errorMessage, containsString("プログラムの最後にゴミがあります"));
        }
    }
}
