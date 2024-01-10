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

		if (!DeclItem.isFirst(tk)) {
			pcx.fatalError("intの次はdeclItemです");
		}
		declItems.addLast(new DeclItem(pcx));
		declItems.getLast().parse(pcx);

		tk = ct.getCurrentToken(pcx); // ,か;を読む

		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!DeclItem.isFirst(tk)) {
				pcx.warning(",の次はdeclItemです");
				ct.skipToLineEndSemi(pcx);
				break;
			}
			declItems.addLast(new DeclItem(pcx));
			try {
				declItems.getLast().parse(pcx);
			} catch (RecoverableErrorException e) {
				pcx.warning(";までスキップしました");
				ct.skipTo(pcx, CToken.TK_SEMI);
			}

			tk = ct.getCurrentToken(pcx); // ,か;を読む
		}

		if (tk.getType() != CToken.TK_SEMI) {
			pcx.fatalError(";が必要です");
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
			for (CParseRule d : declItems) {
				d.codeGen(pcx);
			}
		}
		o.println(";;; intDecl completes");
	}
}
