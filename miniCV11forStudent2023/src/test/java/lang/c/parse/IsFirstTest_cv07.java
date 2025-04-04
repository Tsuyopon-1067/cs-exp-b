package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lang.IOContext;
import lang.InputStreamForTest;
import lang.PrintStreamForTest;
import lang.c.CParseContext;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;
import lang.c.parse.statement.StatementBlock;
import lang.c.parse.statement.StatementIf;
import lang.c.parse.statement.StatementInput;
import lang.c.parse.statement.StatementOutput;
import lang.c.parse.statement.StatementWhile;
import lang.c.parse.condition.ConditionBlock;
import lang.c.parse.statement.Statement;

public class IsFirstTest_cv07 {
    // Test that each class's isFirst() is valid
    // Distant future, you should add necessary test cases to each Test code.

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
    public void testStatement() {
        String[] testDataArr = { "if", "while", "input", "output", };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, Statement.isFirst(firstToken), is(true));
        }
    }

    @Test
    public void testStatementIf() {
        String testData = "if";
        resetEnvironment();
        inputStream.setInputString(testData);
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(testData, StatementIf.isFirst(firstToken), is(true));
    }

    @Test
    public void testStatementWhile() {
        String testData = "while";
        resetEnvironment();
        inputStream.setInputString(testData);
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(testData, StatementWhile.isFirst(firstToken), is(true));
    }

    @Test
    public void testStatementInput() {
        String testData = "input";
        resetEnvironment();
        inputStream.setInputString(testData);
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(testData, StatementInput.isFirst(firstToken), is(true));
    }

    @Test
    public void testStatementOutput() {
        String testData = "output";
        resetEnvironment();
        inputStream.setInputString(testData);
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(testData, StatementOutput.isFirst(firstToken), is(true));
    }

    @Test
    public void testStatementBlock() {
        String[] testDataArr = { "{ input i_variable; }", };
        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat(testData, StatementBlock.isFirst(firstToken), is(true));
        }
    }

    @Test
    public void testConditionBlock() {
        String testData = "( i_a==1 )";
        resetEnvironment();
        inputStream.setInputString(testData);
        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(testData, ConditionBlock.isFirst(firstToken), is(true));
    }

}
