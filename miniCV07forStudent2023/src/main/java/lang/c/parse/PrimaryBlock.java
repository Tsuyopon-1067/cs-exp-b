package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class PrimaryBlock extends CParseRule {
    // primaryBlock ::= LPAR primary RPAR
	CParseRule primary;

	public PrimaryBlock(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx);

		if (Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "(の後ろはprimaryです");
		}
		ct.getNextToken(pcx);

		if (tk.getType() != CToken.TK_RPAR) {
			pcx.fatalError(tk.toExplainString() + "primaryの後ろは)です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; PrimaryBlock starts");
		if (primary != null) {
			primary.codeGen(pcx);
		}
		o.println(";;; PrimaryBlock completes");
	}
}