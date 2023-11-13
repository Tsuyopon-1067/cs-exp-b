package lang.c;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestHelperTest {

    TestHelper helper = null;

    @Before
    public void setUp() {
        helper = new TestHelper();
    }

    @After
    public void tearDown() {
        helper = null;
    }


    @Test
    public void convertToValidateSimple() {
        LinkedList<String> testData = new LinkedList<String>();
        testData.add("; comment from head of line");
        testData.add("LABEL:     ; comment in the line");
        testData.add("  MOV R0, R1  ; SPACE ; comment in the line");
        testData.add("\tMOV\tR0,\tR1\t; SPACE ; comment in the line");

        LinkedList<String> actual = helper.convertToValidateData(testData);
        assertThat(actual.size(), is(3));
        assertThat(actual.get(0), is("LABEL:"));
        assertThat(actual.get(1), is("MOVR0,R1"));
        assertThat(actual.get(2), is("MOVR0,R1"));
    }

    @Test
    public void convertToValidateActual() {
        String testData[] = {
            ";;; expression starts",
            ";;; term starts",
            ";;; factor starts",
            ";;; number starts",
            "	MOV	#13, (R6)+	; Number: 数を積む[[1行目,1文字目の'13']]",
            ";;; number completes",
            ";;; factor completes",
            ";;; term completes",
            ";;; term starts",
            ";;; factor starts",
            ";;; number starts",
            "	MOV	#7, (R6)+	; Number: 数を積む[[1行目,6文字目の'7']]",
            ";;; number completes",
            ";;; factor completes",
            ";;; term completes",
            "	MOV	-(R6), R0	; ExpressionAdd: ２数を取り出して、足し、積む[[1行目,4文字目の'+']]",
            "	MOV	-(R6), R1	; ExpressionAdd:",
            "	ADD	R1, R0	; ExpressionAdd:",
            "	MOV	R0, (R6)+	; ExpressionAdd:",
            ";;; term starts",
            ";;; factor starts",
            ";;; number starts",
            "	MOV	#2, (R6)+	; Number: 数を積む[[1行目,10文字目の'2']]",
            ";;; number completes",
            ";;; factor completes",
            ";;; term completes",
            "	MOV	-(R6), R0	; ExpressionAdd: ２数を取り出して、足し、積む[[1行目,8文字目の'+']]",
            "	MOV	-(R6), R1	; ExpressionAdd:",
            "	ADD	R1, R0	; ExpressionAdd:",
            "	MOV	R0, (R6)+	; ExpressionAdd:",
            ";;; expression completes"
        };
        String expected[] = {
            "MOV#13,(R6)+",
            "MOV#7,(R6)+",
            "MOV-(R6),R0",
            "MOV-(R6),R1",
            "ADDR1,R0",
            "MOVR0,(R6)+",
            "MOV#2,(R6)+",
            "MOV-(R6),R0",
            "MOV-(R6),R1",
            "ADDR1,R0",
            "MOVR0,(R6)+"
        };
        LinkedList<String> actual = helper.convertToValidateData(new LinkedList<String>(Arrays.asList(testData)));
        assertThat(actual.size(), is(expected.length));
        for ( int i=0; i<expected.length; i++ ) {
            String expectedLine = expected[i];
            assertThat(expectedLine, is(actual.get(i)));
        }
    }
}
