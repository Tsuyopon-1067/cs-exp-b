package lang.c.parse.statement;

import java.io.PrintStream;
import java.util.ArrayDeque;
import lang.c.parse.*;

import lang.*;
import lang.c.*;

public class StatementReturn extends CParseRule {
    // statementReturn ::= RETURN [ expression ] SEMI
	ArrayDeque<CParseRule> expressionList = new ArrayDeque()<CParseRule>();

	public StatementReturn(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_RETURN;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // returnを読み飛ばす

		while (Expression.isFirst(tk)) {
			expressionList.add(new Expression(pcx));
			expressionList.getLast().parse(pcx);
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
		}

		if (tk.getType() != CToken.TK_SEMI) {
			pcx.recoverableError(tk.toExplainString() + "文末は;です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expressionList != null) {
			for (CParseRule expression : expressionList) {
				expression.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementReturn starts");
		if (expressionList != null) {
			for (CParseRule expression : expressionList) {
				expression.codeGen(pcx);
			}
		}
		o.println(";;; StatementReturn completes");
	}
}