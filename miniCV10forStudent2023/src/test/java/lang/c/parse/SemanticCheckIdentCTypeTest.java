package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

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
import lang.c.CType;

/**
 * Before Testing Semantic Check by using this testing class, All ParseTest must be passed.
 * Bacause this testing class uses parse method to create testing data.
 */
public class SemanticCheckIdentCTypeTest {
    // Test that CType is set correctly. For "cv04"

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

    @Ignore
    @Test
    public void type() throws FatalErrorException {
        ArrayList<TestDataSet> testDataSetArr = new ArrayList<TestDataSet>();
        testDataSetArr.add(new TestDataSet("i_ABC", CType.T_int, false));
        testDataSetArr.add(new TestDataSet("ip_ABC", CType.T_pint, false));
        testDataSetArr.add(new TestDataSet("ia_ABC", CType.T_int_array, false));
        testDataSetArr.add(new TestDataSet("ipa_ABC", CType.T_pint_array, false));
        testDataSetArr.add(new TestDataSet("c_ABC", CType.T_int, true));

        for ( TestDataSet testDataSet: testDataSetArr ) {
            resetEnvironment();
            inputStream.setInputString(testDataSet.testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testDataSet.testData, Ident.isFirst(firstToken), is(true));
            Ident cp = new Ident(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            assertThat(testDataSet.testData, cp.getCType().getType(), is(testDataSet.type));
            assertThat(testDataSet.testData, cp.isConstant(), is(testDataSet.isConstant));
        }
    }

    // IdentではなくVariableにすべき？
    @Ignore
    @Test
    public void variableType() throws FatalErrorException {
        ArrayList<TestDataSet> testDataSetArr = new ArrayList<TestDataSet>();
        testDataSetArr.add(new TestDataSet("ia_ABC[123]", CType.T_int, false));
        testDataSetArr.add(new TestDataSet("ipa_ABC[123]", CType.T_pint, false));

        for ( TestDataSet testDataSet: testDataSetArr ) {
            resetEnvironment();
            inputStream.setInputString(testDataSet.testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testDataSet.testData, Variable.isFirst(firstToken), is(true));
            Variable cp = new Variable(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            assertThat(testDataSet.testData, cp.getCType().getType(), is(testDataSet.type));
            assertThat(testDataSet.testData, cp.isConstant(), is(testDataSet.isConstant));
        }
    }

    // IdentではなくPrimaryにすべき？
    @Ignore
    @Test
    public void primaryType() throws FatalErrorException {
        ArrayList<TestDataSet> testDataSetArr = new ArrayList<TestDataSet>();
        testDataSetArr.add(new TestDataSet("*ip_ABC", CType.T_int, false));
        testDataSetArr.add(new TestDataSet("*ipa_ABC[123]", CType.T_int, false));

        for ( TestDataSet testDataSet: testDataSetArr ) {
            resetEnvironment();
            inputStream.setInputString(testDataSet.testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testDataSet.testData, Primary.isFirst(firstToken), is(true));
            Primary cp = new Primary(cpContext);

            cp.parse(cpContext);
            cp.semanticCheck(cpContext);
            assertThat(testDataSet.testData, cp.getCType().getType(), is(testDataSet.type));
            assertThat(testDataSet.testData, cp.isConstant(), is(testDataSet.isConstant));
        }
    }
}

class TestDataSet {
    String testData;
    int type;
    boolean isConstant;

    TestDataSet(String testData, int type, boolean isConstant) {
        this.testData = testData;
        this.type = type;
        this.isConstant = isConstant;
    }
}
