package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.parse.statement.StatementInput;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckInput {
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

    void resetEnvironment() {
        tearDown();
        setUp();
    }

    // 正当のテストコード例
    @Test
    public void semanticCheckInputTrue() throws FatalErrorException {
        String[] testDataArr = {
            "input i_a;",
            "input ip_a;",
            "input *ip_a;",
            "input ia_a[3];",
            "input ipa_a[3];",
            "input *ipa_a[3];",
        };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            String errorOutput = errorOutputStream.getPrintBufferString();
            assertThat(errorOutput, is(""));

        }
    }

    // 不当のテストコード例
    @Test
    public void semanticCheckInputFalse() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("input c_a;", "定数には代入できません"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData.getTestStr(), StatementInput.isFirst(firstToken), is(true));
            StatementInput cp = new StatementInput(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData.getTestStr() + ". FatalError" + testData.getMsg() + ".Exception should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }

}
