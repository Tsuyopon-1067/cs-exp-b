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
import lang.RecoverableErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenRule;
import lang.c.CTokenizer;

public class ParseDeclBlockTestCv11 {

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
    public void parseDeclBlockTest() throws FatalErrorException {
        String[] testDataArr = {
            """
                int a;
                {
                int a;
                int b;
                int c[5];
                int d;

                a = 1;
                b = 2;
                c[2] = 3;
                d = 4;
                }
                {
                int c;
                int d;

                c = 5;
                d = 6;
                }
                    """
                        ,
                """
                int a, b[10];
                const int c=10;
                {
                    int e;
                    int *f;
                    const int g=10;

                    {
                        int z;
                    }

                    {
                        int y=0;
                        a = 100;
                    }
                }
                    """
        };

        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            CParseRule cp = new Program(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void parseDeclBlockErr() throws FatalErrorException {
        HelperTestStrMsg[] testDataArr = {
            new HelperTestStrMsg(
                """
                int a, b[10];
                const int c=10;
                {
                    a=1;
                    """,
                "}が閉じていません"),
            new HelperTestStrMsg(
                """
                    {
                        int a;
                        a=1;
                        int b;
                    }
                        """,
                "}が閉じていません"),

        };
        for ( HelperTestStrMsg testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData.getTestStr());
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            CParseRule cp = new Program(cpContext);

            try {
                cp.parse(cpContext);
                cp.semanticCheck(cpContext);
                //fail("Failed with " + testData.getTestStr() + ". FatalErrorException should be invoked");
            } catch ( RecoverableErrorException e ) {
                assertThat(e.getMessage(), containsString(testData.getMsg()));
            }
        }
    }
}
