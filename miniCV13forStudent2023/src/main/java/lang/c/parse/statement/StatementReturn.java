package lang.c.parse.statement;

import java.io.PrintStream;
import lang.c.parse.*;

import lang.*;
import lang.c.*;

public class StatementReturn extends CParseRule {
    // statementReturn ::= RETURN [ expression ] SEMI
	CParseRule expression;
	FunctionInfo functionInfo;

	public StatementReturn(CParseContext pcx, FunctionInfo functionInfo) {
		this.functionInfo = functionInfo;
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_RETURN;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		functionInfo.setTrueToIsExistReturn();
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // returnを読み飛ばす

		if (Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
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
			if (expression.getCType() != functionInfo.getReturnType()) {
				String warningMessage = String.format("StatementReturn: 戻り値の型が関数%sの型と一致しません", functionInfo.getName());
				pcx.warning(warningMessage);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementReturn starts");
		if (expression != null) {
			expression.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; StatementReturn: スタックに積まれた値を戻り値用レジスタR0に移す（returnなしでも次にR6を復帰するので問題なし）");
		}
		o.println(String.format("\tJMP\t%s\t; StatementReturn: 関数のRET命令にジャンプする", functionInfo.getReturnLabel()));
		o.println(";;; StatementReturn completes");
	}
}