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
			pcx.fatalError(tk.toExplainString() + "Primaryの後ろは=です");
		}

		tk = ct.getNextToken(pcx);
		try {
			if (!Expression.isFirst(tk)) {
				pcx.recoverableError(tk.toExplainString() + "=の後ろはExpressionです");
			}
			expression = new Expression(pcx);
			expression.parse(pcx);
		} catch (RecoverableErrorException e) {
			//ct.skipToLineEndSemi(pcx);
			//pcx.warning("StatementAssign: 行末または;までスキップしました");
			//tk = ct.getCurrentToken(pcx);
			tk = ct.getCurrentToken(pcx);
			int lineNo = tk.getLineNo();
			while (tk.getType() != CToken.TK_SEMI && !Statement.isFirst(tk) && tk.getType() != CToken.TK_EOF) {
				tk = ct.getNextToken(pcx);
				if (tk.getLineNo() != lineNo) {
					break; // 改行したら抜ける
				}
			}
			pcx.warning("StatementAssign: 文をスキップしました");
			tk = ct.getCurrentToken(pcx);
			System.out.printf("%s, %s\n", tk.toDetailExplainString(), tk.getText());
			return;
		}

		tk = ct.getCurrentToken(pcx); // Expressionは次の字句まで読んでしまう
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.recoverableError("StatementAssign : " + tk.toExplainString() + ";がありません");
			int lineNo = tk.getLineNo();
			while (tk.getType() != CToken.TK_SEMI && !Statement.isFirst(tk) && tk.getType() != CToken.TK_EOF) {
				tk = ct.getNextToken(pcx);
				if (tk.getLineNo() != lineNo) {
					break; // 改行したら抜ける
				}
			}
			pcx.warning("ExpressionAddまたはExpressionSubをスキップしました");
			return;
		} else {
			tk = ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null && expression != null) {
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx);

			if (primary.getCType() == null) {
				primary.setCType(CType.getCType(CType.T_err));
			}
			if (expression.getCType() == null) {
				expression.setCType(CType.getCType(CType.T_err));
			}

			if (primary.getCType() != expression.getCType()) {
				pcx.warning(op.toExplainString() + "左辺の型[" + primary.getCType().toString()
					+ "]と右辺の型[" + expression.getCType().toString() + "]が異なります");
			}
			if (primary.isConstant()) {
				pcx.warning(primaryToken.toExplainString() + "左辺が定数です");
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