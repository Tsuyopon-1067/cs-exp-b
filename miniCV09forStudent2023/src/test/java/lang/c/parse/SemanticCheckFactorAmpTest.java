package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import lang.FatalErrorException;
import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckFactorAmpTest {
    // 下記テストコード例を用いてCの規則にしたがったテストを書くこと
    // 2+1, 2+&1, &2+1, &2+&1
    // 2-1, 2-&1, &2-1, &2-&1
    // &3-1-&1, &3-&1-&1, 1+&

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
    public void semanticCheckTrueExample() throws FatalErrorException {
        String[] testDataArr = {"2+1", "&2+1", "2-1", "&2-1", "&2-&1", "&3-1-&1" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            String errorOutput = errorOutputStream.getPrintBufferString();
            assertThat(errorOutput, is(""));

        }
    }

    // 不当のテストコード例
    @Ignore
    @Test
    public void semanticCheckFalseExample() throws FatalErrorException {
        String[] testDataArr = {"2-&1", "&3-&1-&1", "1+&" };
        String[] errMessageArr = {
            "左辺の型[int]と右辺の型[int*]は引けません",
            "左辺の型[int]と右辺の型[int*]は引けません",
            "&の後はNumberかPrimaryです",
    };
        for (int i = 0; i < testDataArr.length; i++) {
            resetEnvironment();
            inputStream.setInputString(testDataArr[i]);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testDataArr[i], Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);
            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testDataArr[i] + ". FatalError" + testDataArr[i] + ".Exception should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(errMessageArr[i]));
            }
        }
    }

}
