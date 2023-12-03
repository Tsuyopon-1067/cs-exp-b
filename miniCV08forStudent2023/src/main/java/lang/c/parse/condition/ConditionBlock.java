package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.BitExpression;

public class ConditionBlock extends CParseRule {
    // conditionBlock  ::= LPAR bitExpression RPAR
	CParseRule expression;

	public ConditionBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // (を読み飛ばす

		if (BitExpression.isFirst(tk)) {
			expression = new BitExpression(pcx);
			expression.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "(の後ろはbitExpressionです");
		}
		tk = ct.getCurrentToken(pcx); // conditionは次の字句まで読んでしまう

		if (tk.getType() != CToken.TK_RPAR) {
			pcx.fatalError(tk.toExplainString() + ")が閉じていません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementBlock starts");
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println(";;; StatementBlock completes");
	}
}