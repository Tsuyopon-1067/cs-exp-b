package lang.c.parse.statement;

import java.io.PrintStream;
import lang.c.parse.*;

import lang.*;
import lang.c.*;

public class StatementReturn extends CParseRule {
    // statementReturn ::= RETURN [ expression ] SEMI
	CParseRule expression;

	public StatementReturn(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_RETURN;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // returnを読み飛ばす

		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "returnの後ろはexpressionです");
		}

		if (tk.getType() != CToken.TK_SEMI) {
			pcx.warning(tk.toExplainString() + "文末は;です");
		} else {
			ct.skipToLineEndSemi(pcx);
			return;
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementReturn starts");
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println(";;; StatementReturn completes");
	}
}