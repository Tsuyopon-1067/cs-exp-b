package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class IntDecl extends CParseRule {
	// intDecl     ::= INT declItem { COMMA declItem } SEMI
	ArrayDeque<CParseRule> declItems;

	public IntDecl(CParseContext pcx) {
		declItems = new ArrayDeque<>();
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // intを読み飛ばす

		try {
			if (!DeclItem.isFirst(tk)) {
				pcx.recoverableError("intの次はdeclItemです");
			}
		} catch (RecoverableErrorException e) {
			ct.skipTo(pcx, CToken.TK_SEMI);
			return;
		}
		declItems.addLast(new DeclItem(pcx));
		declItems.getLast().parse(pcx);

		tk = ct.getCurrentToken(pcx); // ,か;を読む

		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!DeclItem.isFirst(tk)) {
				pcx.recoverableError(",の次はdeclItemです");
			}
			declItems.addLast(new DeclItem(pcx));
			try {
				if (!DeclItem.isFirst(tk)) {
					declItems.getLast().parse(pcx);
				}
			} catch (RecoverableErrorException e) {
				pcx.warning(";までスキップしました");
				ct.skipTo(pcx, CToken.TK_SEMI);
			}

			tk = ct.getCurrentToken(pcx); // ,か;を読む
		}

		if (tk.getType() != CToken.TK_SEMI) {
			ct.skipTo(pcx, CToken.TK_SEMI);
			pcx.recoverableError(";が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (declItems != null) {
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; intDecl starts");
		if (declItems != null) {
		}
		o.println(";;; intDecl completes");
	}
}
