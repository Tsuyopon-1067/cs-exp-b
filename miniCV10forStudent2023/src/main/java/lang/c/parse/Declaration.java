package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class Declaration extends CParseRule {
	// declaration ::= intDecl | constDecl
	boolean isIntDecl;
	CParseRule nextParseRule;

	public Declaration(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if (IntDecl.isFirst(tk)) {
			isIntDecl = true;
			nextParseRule = new IntDecl(pcx);
		} else {
			isIntDecl = false;
			nextParseRule = new ConstDecl(pcx);
		}
		try {
			nextParseRule.parse(pcx);
		} catch (RecoverableErrorException e) {
			pcx.warning("宣言をスキップしました");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nextParseRule != null) {
			nextParseRule.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; declaration starts");
		if (nextParseRule != null) {
			nextParseRule.codeGen(pcx);
		}
		o.println(";;; declaration completes");
	}
}
