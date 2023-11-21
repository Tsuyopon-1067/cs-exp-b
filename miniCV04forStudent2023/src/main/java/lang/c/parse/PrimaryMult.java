package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class PrimaryMult extends CParseRule {
	// primaryMult    ::= MULT variable
	CToken op;
	CParseRule variable;

	public PrimaryMult(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		// *の次の字句を読む
		ct.getNextToken(pcx);
		variable = new Variable(pcx);
		variable.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (variable != null) {

		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primaryMult starts");
		if (variable != null) {

		}
		o.println(";;; primaryMult completes");
	}
}
