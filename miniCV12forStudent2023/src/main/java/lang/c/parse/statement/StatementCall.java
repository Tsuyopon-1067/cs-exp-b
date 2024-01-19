package lang.c.parse.statement;

import java.io.PrintStream;
import java.util.ArrayDeque;
import lang.c.parse.*;

import lang.*;
import lang.c.*;

public class StatementCall extends CParseRule {
    // statementCall   ::= CALL ident LPAR RPAR SEMI
	CParseRule call, ident;
	private String functionName;

	public StatementCall(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CALL;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // callを読み飛ばす

		if (Ident.isFirst(tk)) {
			ident = new Ident(pcx);
			ident.parse(pcx);
			functionName = tk.getText();
		} else {
			pcx.recoverableError(tk.toExplainString() + "callの後ろはidentです");
		}

		tk = ct.getNextToken(pcx);
		if (tk.getType() != CToken.TK_LPAR) {
			pcx.recoverableError("identの後ろは(です");
		}
		tk = ct.getNextToken(pcx); // (を読み飛ばす
		if (tk.getType() != CToken.TK_RPAR) {
			pcx.recoverableError("()が閉じていません");
		}

		tk = ct.getNextToken(pcx);
		if (tk.getType() != CToken.TK_SEMI) {
			pcx.recoverableError("StatementCall : " + tk.toExplainString() + ";がありません");
		} else {
			tk = ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		ident.semanticCheck(pcx);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementCall starts");
		o.println("\tJSR\t#" + functionName + "\t; Ident: 関数へジャンプ");
		o.println(";;; StatementCall completes");
	}
}