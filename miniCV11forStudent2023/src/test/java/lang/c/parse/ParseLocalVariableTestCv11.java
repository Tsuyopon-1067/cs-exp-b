package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import lang.c.parse.statement.StatementInput;
import lang.c.parse.statement.StatementOutput;

public class ParseLocalVariableTestCv11 {

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
    public void parseVariableCorrect() throws FatalErrorException {
        String[] testDataArr = {
            """
            int a, b[10];
            const int c=10;
            {
                int e;
                int *f;
                const int g=10;

                e = 0;
            }
            {
                const int y=0;
                a = y;
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
            } catch ( FatalErrorException e ) {
                fail("Failed with " + testData + ". Please modify this Testcase to pass.");
            }
        }
    }

    @Test
    public void parseLocalVariableError() throws FatalErrorException {
        String[] testDataArr = {
            """
            int a, b[10];
            const int c=10;
            {
                int e;
                int *f;
                const int g=10;

                z = 0;
            }
            {
                const int y=0;
                a = y;
            }
                    """
        };

        for ( String testData: testDataArr ) {
            resetEnvironment();
            inputStream.setInputString(testData);
            CToken firstToken = tokenizer.getNextToken(cpContext);
            assertThat("Failed with " + testData, Program.isFirst(firstToken), is(true));
            CParseRule cp = new Program(cpContext);
            cp.parse(cpContext);
            String msg = cpContext.getIOContext().getErrStream().toString();
            writeTextFile(msg);
            assertThat("Failed with " + testData, msg.equals("変数zは宣言されていません"), is(true));
        }
    }

    private void writeTextFile(String str) {
        try {
            // FileWriterクラスのオブジェクトを生成する
            FileWriter file = new FileWriter("/Users/tsuyopon/Desktop/test.txt");
            // PrintWriterクラスのオブジェクトを生成する
            PrintWriter pw = new PrintWriter(new BufferedWriter(file));

            //ファイルに書き込む
            pw.println(str);

            //ファイルを閉じる
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}