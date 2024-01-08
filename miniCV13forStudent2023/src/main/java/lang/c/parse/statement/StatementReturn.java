package lang.c.parse.statement;

import java.io.PrintStream;
import lang.c.parse.*;

import lang.*;
import lang.c.*;

public class StatementReturn extends CParseRule {
    // statementReturn ::= RETURN [ expression ] SEMI
	CParseRule expression;
	String returnLabel;

	public StatementReturn(CParseContext pcx, String returnLabel) {
		this.returnLabel = returnLabel;
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
			tk = ct.getCurrentToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "returnの後ろはexpressionです");
		}

		if (tk.getType() != CToken.TK_SEMI) {
			pcx.warning("StatementReturn" + tk.toExplainString() + "文末は;です");
			ct.skipToLineEndSemi(pcx);
			return;
		}
		tk = ct.getNextToken(pcx);
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
			o.println("\tMOV\t-(R6), R0\t; StatementReturn: スタックに積まれた値を戻り値用レジスタR0に移す");
		}
		o.println(String.format("\tJUMP\t%s\t; StatementReturn: 関数のRET命令にジャンプする", returnLabel));
		o.println(";;; StatementReturn completes");
	}
}