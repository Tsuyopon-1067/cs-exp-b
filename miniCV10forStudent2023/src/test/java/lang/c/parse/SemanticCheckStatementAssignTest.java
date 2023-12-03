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
import lang.c.parse.statement.StatementAssign;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckStatementAssignTest {

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

    @Test
    public void SemanticCheckAssignIntegerTypeOK() throws FatalErrorException {
        String[] testDataArr = {"i_A=i_B;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void SemanticCheckAssignPointerTypeOK() throws FatalErrorException {
        String[] testDataArr = {"ip_A=ip_B;", "*ip_A=i_A;", "ip_A=&i_A;", "ip_a=&1;"};
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    // (1) 整数型の扱い
    // If it is difficult to understand, separate the test cases and create a new test.
    @Ignore
    @Test
    public void SemanticCheckAssignIntegerTypeError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("*i_a=1;", "ポインタではない数値でアドレスを参照することはできません"),
            new HelperTestStrMsg("i_a[3]=1;", "identの型が配列型ではありません"),
            new HelperTestStrMsg("i_a=&1;", "左辺の型[int]と右辺の型[int*]が異なります"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }

    // (2) ポインタ型の扱い
    @Ignore
    @Test
    public void SemanticCheckAssignPinterTypeError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("ip_a=1;", "左辺の型[int*]と右辺の型[int]が異なります"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }

    // (3) 配列型の扱い
    @Ignore
    @Test
    public void SemanticCheckAssignArrayTypeError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("ia_a=1;", "配列のインデックスが指定されていません"),
            new HelperTestStrMsg("ia_a=ia_a;", "配列のインデックスが指定されていません"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }

    // (3) ポインタ配列型の扱い
    @Ignore
    @Test
    public void SemanticCheckAssignPointArrayTypeError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("ipa_a=&1;", "配列のインデックスが指定されていません"),
            new HelperTestStrMsg("ipa_a=ipa_a;", "配列のインデックスが指定されていません"),
            new HelperTestStrMsg("*ipa_a[3]=&3;", "左辺の型[int]と右辺の型[int*]が異なります"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }

    // (5) 定数には代入できないことの確認
    @Ignore
    @Test
    public void SemanticCheckAssignConstantTypeError() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = { new HelperTestStrMsg("c_a=1;", "左辺が定数です") };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }

    // (extra) code should be written as follows
    @Ignore
    @Test
    public void SemanticCheckAssignExtra() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg("i_A=ip_B;", "左辺の型[int]と右辺の型[int*]が異なります"),
            new HelperTestStrMsg("ip_A=i_B;", "左辺の型[int*]と右辺の型[int]が異なります"),
            new HelperTestStrMsg("*ip_A=ip_A;", "左辺の型[int]と右辺の型[int*]が異なります"),
            new HelperTestStrMsg("i_A=&i_B;", "左辺の型[int]と右辺の型[int*]が異なります"),
        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, StatementAssign.isFirst(firstToken), is(true));
            StatementAssign cp = new StatementAssign(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                fail("Failed with " + testData + ". FatalErrorException should be invoked");
            } catch ( FatalErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }
}