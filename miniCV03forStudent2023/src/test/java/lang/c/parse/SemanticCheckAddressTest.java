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
import lang.c.CType;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckAddressTest {
    // Test that Condition node's semanticCheck is valid.

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

    // TestCase02.c を見てどれが正当でどれが不当か考えて以下テストコードの
    // testDataArr に追加してください．
    // 意味解析 正当
    @Test
    public void semanticCheckAddressAddAccept() throws FatalErrorException {
        String[] testDataArr = { "2 + 1" };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    // 意味解析 不当
    @Test
    public void semanticCheckAddressAddError()  {
        String[] testDataArr = { "&2 + &1" };
        String[] errMessageArr = {"左辺の型[int*]と右辺の型[int*]は足せません",};
        for (int i = 0; i < testDataArr.length; i++) {
            resetEnvironment();
            inputStream.setInputString(testDataArr[i]);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testDataArr[i], Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testDataArr[i] + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(errMessageArr[i]));
            }
        }
    }

    // 意味解析 正当
    @Test
    public void semanticCheckAddressSubAccept() throws FatalErrorException {
        TestStrType[] testDataArr = {
            new TestStrType("2 + 1", CType.T_int),
            new TestStrType("2 - 1", CType.T_int),
            new TestStrType("&2 + 1", CType.T_pint),
            new TestStrType("2 + &1", CType.T_pint),
            new TestStrType("&2 - 1", CType.T_pint),
            new TestStrType("&2 - &1", CType.T_int),
        };
        for ( TestStrType testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData.getStr(), Expression.isFirst(firstToken), is(true));
            Expression cp = new Expression(cpContext);
            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            assertThat(cp.getCType().getType(), is(testData.getType()));
        }
    }

    // 意味解析 不当
    @Test
    public void semanticCheckAddressSubError()  {
        String[] testDataArr = { "2 - &1" };
        String[] errMessageArr = {"左辺の型[int]と右辺の型[int*]は引けません",};

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

class TestStrType {
    private String str;
    private int type;
    public TestStrType(String str, int type) {
        this.str = str;
        this.type = type;
    }
    public String getStr() {
        return str;
    }
    public int getType() {
        return type;
    }

}