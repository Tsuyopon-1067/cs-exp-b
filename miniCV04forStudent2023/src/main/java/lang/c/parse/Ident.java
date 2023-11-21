package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
	// ident ::= IDENT
	CToken ident;

	public Ident(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			String identText = ident.getText();
			int setType = CType.T_int;
			boolean isConstant = false;
			if (identText.startsWith("i_")) {
				setType = CType.T_int;
			} else if (identText.startsWith("ip_")) {
				setType = CType.T_pint;
			} else if (identText.startsWith("ia_")) {
				setType = CType.T_int_array;
			} else if (identText.startsWith("ipa_")) {
				setType = CType.T_pint_array;
			} else if (identText.startsWith("c_")) {
				setType = CType.T_int;
				isConstant = true;
			} else {
				pcx.fatalError("変数はi_，ip_，ia_，ipa_，c_のどれかから始まる必要があります");
			}

			this.setCType(CType.getCType(setType));
			this.setConstant(isConstant);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ident starts");
		if (ident != null) {
		}
		o.println(";;; ident completes");
	}
}
