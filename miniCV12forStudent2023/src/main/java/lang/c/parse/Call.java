package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Call extends CParseRule {
	// call            ::= LPAR RPAR
	CToken num;

	public Call(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LPAR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // (を読み飛ばす

		if (tk.getType() != CToken.TK_RPAR) {
			pcx.recoverableError(tk.toDetailExplainString() + "()が閉じていません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
	}
}
