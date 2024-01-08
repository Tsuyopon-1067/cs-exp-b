package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class Declaration extends CParseRule {
	// declaration     ::= intDecl | constDecl | voidDecl
	CParseRule nextParseRule;

	public Declaration(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk) || VoidDecl.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if (IntDecl.isFirst(tk)) {
			nextParseRule = new IntDecl(pcx);
		} else if (ConstDecl.isFirst(tk)) {
			nextParseRule = new ConstDecl(pcx);
		} else {
			System.err.println("declaration " + tk.toDetailExplainString());
			nextParseRule = new VoidDecl(pcx);
		}
		try {
			nextParseRule.parse(pcx);
		} catch (RecoverableErrorException e) {
			pcx.warning("宣言をスキップしました");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (nextParseRule != null) {
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