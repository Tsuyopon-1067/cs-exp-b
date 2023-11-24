package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Primary extends CParseRule {
	// primary        ::= primaryMult | variable
	private CParseRule nextParseRule;
	private boolean isPrimaryMult; // FactorAmp = AMP primary, primary = primaryMult の場合の検出に使う

	public Primary(CParseContext pcx) {
	}

	public boolean isPrimaryMult() {
		return isPrimaryMult;
	}

	public static boolean isFirst(CToken tk) {
		return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();

		if (ct.getCurrentToken(pcx).getType() == CToken.TK_MULT) {
			isPrimaryMult = true;
			nextParseRule = new PrimaryMult(pcx);
		} else {
			isPrimaryMult = false;
			nextParseRule = new Variable(pcx);
		}
		nextParseRule.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nextParseRule != null) {
			nextParseRule.semanticCheck(pcx);
			setCType(nextParseRule.getCType());
			setConstant(false);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (nextParseRule != null) {
			nextParseRule.codeGen(pcx);
		}
		o.println(";;; primary completes");
	}
}