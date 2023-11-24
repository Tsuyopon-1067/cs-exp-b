package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class StatementAssign extends CParseRule {
    // statementAssign ::= primary ASSIGN expression SEMI（注）ASSIGN=’=’, SEMI=’;’
    CParseRule primary, expression;

	public StatementAssign(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		primary = new Primary(pcx);
		primary.parse(pcx);

		tk = ct.getNextToken(pcx);
		if (tk.getType() != CToken.TK_ASSIGN) {
			pcx.fatalError(tk.toExplainString() + "Statementの後ろは=です");
		}

		tk = ct.getNextToken(pcx);
		if (!Expression.isFirst(tk)) {
			pcx.fatalError(tk.toExplainString() + "=の後ろはExpressionです");
		}
		expression = new Expression(pcx);
		expression.parse(pcx);

		tk = ct.getNextToken(pcx);
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Statement starts");
		o.println(";;; Statement completes");
	}
}