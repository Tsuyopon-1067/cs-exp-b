package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Expression;

public class StatementOutput extends CParseRule {
    // statementOutput ::= OUTPUT expression SEMI
	CParseRule expression;

	public StatementOutput(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_OUTPUT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);

		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "outputの後ろはexpressionです");
		}

		tk = ct.getCurrentToken(pcx); // Expressionは次の字句まで読んでしまう
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.fatalError(tk.toExplainString() + "文末は;です");
		}
		ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementOutput starts");
		if (expression != null) {
			expression.codeGen(pcx);
			o.println("\tMOV\t#0xFFE0, R0\t; StatementOutput: IOアドレスをR0に確保");
			o.println("\tMOV\t-(R6), (R0)\t; StatementOutput: expressionの値をIOアドレスに代入する");
		}
		o.println(";;; StatementOutput completes");
	}
}