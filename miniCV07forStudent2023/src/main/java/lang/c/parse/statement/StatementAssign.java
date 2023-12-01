package lang.c.parse.statement;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Expression;
import lang.c.parse.Primary;

public class StatementAssign extends CParseRule {
    // statementAssign ::= primary ASSIGN expression SEMI（注）ASSIGN=’=’, SEMI=’;’
    CParseRule primary, expression;
	CToken primaryToken, op;

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
		primaryToken = tk;

		tk = ct.getCurrentToken(pcx);
		op = tk;
		if (tk.getType() != CToken.TK_ASSIGN) {
			pcx.fatalError(tk.toExplainString() + "Statementの後ろは=です");
		}

		tk = ct.getNextToken(pcx);
		if (!Expression.isFirst(tk)) {
			pcx.fatalError(tk.toExplainString() + "=の後ろはExpressionです");
		}
		expression = new Expression(pcx);
		expression.parse(pcx);

		tk = ct.getCurrentToken(pcx);
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.fatalError(tk.toExplainString() + ";がありません");
		}
		ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null && expression != null) {
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx);
			if (primary.getCType() != expression.getCType()) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + primary.getCType().toString()
					+ "]と右辺の型[" + expression.getCType().toString() + "]が異なります");
			}
			if (primary.isConstant()) {
				pcx.fatalError(primaryToken.toExplainString() + "左辺が定数です");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementAssign starts");
		if (primary != null) {
			primary.codeGen(pcx);
		}
		if (expression != null) {
			expression.codeGen(pcx);
		}
		o.println("\tMOV\t-(R6), R1\t; StatementAssign");
		o.println("\tMOV\t-(R6), R0\t; StatementAssign: 左辺のアドレスを取り出す");
		o.println("\tMOV\tR1, (R0)\t; StatementAssign: 変数に値を代入する");
		o.println(";;; StatementAssign completes");
	}
}