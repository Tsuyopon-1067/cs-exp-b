package lang.c.parse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

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
import lang.c.TestHelper;

public class CodeGenProgramTest {

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

    /**
     * This test is similar testing by compile() in MiniCompilerImplTest class.
     * 
     * @throws FatalErrorException
     */
    @Test
    public void codeGenProgramAdd3Terms() throws FatalErrorException {
        inputStream.setInputString("13 + 7 + 2");
        String expected[] = {
                "	. = 0x100",
                "	JMP	__START	; ProgramNode: 最初の実行文へ",
                "__START:",
                "	MOV	#0x1000, R6",
                "	MOV	#13, (R6)+",
                "	MOV	#7, (R6)+",
                "	MOV	-(R6), R0",
                "	MOV	-(R6), R1",
                "	ADD	R1, R0",
                "	MOV	R0, (R6)+",
                "	MOV	#2, (R6)+",
                "	MOV	-(R6), R0",
                "	MOV	-(R6), R1",
                "	ADD	R1, R0",
                "	MOV	R0, (R6)+",
                "	MOV	-(R6), R0",
                "	HLT",
                "	.END"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }

    @Test
    public void codeGenProgramAdd2Terms() throws FatalErrorException {
        inputStream.setInputString("2 + 5");
        String expected[] = {
            "	. = 0x100",
            "	JMP	__START",
            "__START:",
            "	MOV	#0x1000, R6",
            "	MOV	#2, (R6)+",
            "	MOV	#5, (R6)+",
            "	MOV	-(R6), R0",
            "	MOV	-(R6), R1",
            "	ADD	R1, R0",
            "	MOV	R0, (R6)+",
            "	MOV	-(R6), R0",
            "	HLT",
            "	.END"
        };

        // Check only code portion, not validate comments
        CParseRule rule = new Program(cpContext);
        helper.checkCodeGen(expected, rule, cpContext);
    }
    
    @Test // Confirmation that only '2' CodeGen is done after calling Program
    public void codeGenLeftTermAdd2Terms() throws FatalErrorException {
        inputStream.setInputString("2 + 5");
        String expected[] = {
            ";;; term starts",
            ";;; factor starts",
            ";;; number starts",
            "	MOV	#2, (R6)+	; Number: 数を積む[[1行目,1文字目の'2']]",
            ";;; number completes",
            ";;; factor completes",
            ";;; term completes"
        };

        CToken firstToken = tokenizer.getNextToken(cpContext);
        assertThat(Program.isFirst(firstToken), is(true));

        CParseRule cpProgram = new Program(cpContext);
        cpProgram.parse(cpContext);
        cpProgram.semanticCheck(cpContext);
        CParseRule ruleInProgram = ((Expression)((Program)cpProgram).program).expression;
        CParseRule leftRuleInExpressionAdd = ((ExpressionAdd)ruleInProgram).left;

        // call test target
        leftRuleInExpressionAdd.codeGen(cpContext);

        // Check finished without errors
        String errorOutput = errorOutputStream.getPrintBufferString();
        assertThat(errorOutput, is(""));

        // Check only code portion, not validate comments
        List<String> outputBuffer = outputStream.getPrintBuffer();
        helper.checkCodePortion(outputBuffer, expected);
    }
}
