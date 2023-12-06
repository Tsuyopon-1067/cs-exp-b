package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class ConstDecl extends CParseRule {
	// constDecl   ::= CONST INT constItem { COMMA constItem } SEMI
	ArrayDeque<CParseRule> constItems;

	public ConstDecl(CParseContext pcx) {
		constItems = new ArrayDeque<>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // constを読み飛ばす

		try {
			if (tk.getType() != CToken.TK_INT) {
				pcx.recoverableError("constの次はintです");
			}
		} catch (RecoverableErrorException e) {
			ct.skipTo(pcx, CToken.TK_SEMI);
			return;
		}
		tk = ct.getNextToken(pcx); // intを読み飛ばす

		try {
			if (!ConstItem.isFirst(tk)) {
				pcx.recoverableError("intの次はconstItemです");
			}
		} catch (RecoverableErrorException e) {
			ct.skipTo(pcx, CToken.TK_SEMI);
		}
		constItems.addLast(new ConstItem(pcx));
		try {
			constItems.getLast().parse(pcx);
			System.out.println("constdecl 46 try parse "+tk.toDetailExplainString());
		} catch (RecoverableErrorException e) {
			ct.skipTo(pcx, CToken.TK_COMMA, CToken.TK_SEMI);
		}

		tk = ct.getCurrentToken(pcx); // ,か;を読む

		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!ConstItem.isFirst(tk)) {
				pcx.recoverableError(",の次はconstItemです");
			}
			constItems.addLast(new ConstItem(pcx));
			try {
				constItems.getLast().parse(pcx);
			} catch (RecoverableErrorException e) {
				ct.skipTo(pcx, CToken.TK_COMMA, CToken.TK_SEMI);
			}

			tk = ct.getCurrentToken(pcx); // ,か;を読む
		}

		if (tk.getType() != CToken.TK_SEMI) {
			ct.skipTo(pcx, CToken.TK_SEMI);
			pcx.recoverableError(";が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (constItems != null) {
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; constDecl starts");
		if (constItems != null) {
			for (CParseRule c : constItems) {
				c.codeGen(pcx);
			}
		}
		o.println(";;; constDecl completes");
	}
}
