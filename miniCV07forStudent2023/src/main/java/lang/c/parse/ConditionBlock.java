package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionBlock extends CParseRule {
    // conditionBlock ::= LPAR condition RPAR
	CParseRule condition;

	public ConditionBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // (を読み飛ばす

		if (Condition.isFirst(tk)) {
			condition = new Condition(pcx);
			condition.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "(の後ろはconditionです");
		}
		tk = ct.getCurrentToken(pcx);

		if (tk.getType() != CToken.TK_RPAR) {
			pcx.fatalError(tk.toExplainString() + "conditionの後ろは)です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null) {
			condition.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementBlock starts");
		if (condition != null) {
			condition.codeGen(pcx);
		}
		o.println(";;; StatementBlock completes");
	}
}