package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Primary extends CParseRule {
	// primary        ::= primaryMult | variable
	CParseRule nextParseRule;
	boolean isPrimaryMult;

	public Primary(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();

		if (ct.getCurrentToken(pcx).getType() == CToken.TK_AMP) {
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

		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (nextParseRule != null) {
		}
		o.println(";;; primary completes");
	}
}
