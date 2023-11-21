package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class AddressToValue extends CParseRule {
	// addressToValue ::= primary
	CParseRule primary;

	public AddressToValue(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		primary = new Primary(pcx);
		primary.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			setCType(primary.getCType());
			setConstant(primary.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (primary != null) {

		}
		o.println(";;; primary completes");
	}
}
