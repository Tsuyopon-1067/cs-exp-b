package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {
	// declItem    ::= [ MULT ] IDENT [ LBRA NUM RBRA ]
	CParseRule ident, num;
	boolean isExistMult = false;
	boolean isArray = false;

	public DeclItem(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT || tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);

		if (tk.getType() == CToken.TK_MULT) {
			tk = ct.getNextToken(pcx); // *を読み飛ばす
			isExistMult = true;
		}

		try {
			if (tk.getType() == CToken.TK_IDENT) {
				ident = new Ident(pcx);
			} else {
				pcx.recoverableError("左辺に変数が必要です");
			}
		} catch (RecoverableErrorException e) {
		}
		tk = ct.getNextToken(pcx);

		if (tk.getType() == CToken.TK_LBRA) {
			isArray = true;
			tk = ct.getNextToken(pcx); // [を読み飛ばす

			try {
				if (tk.getType() == CToken.TK_NUM) {
					num = new Number(pcx);
				} else {
					pcx.recoverableError("[]内は数値です");
				}
			} catch (RecoverableErrorException e) {
			}
			tk = ct.getNextToken(pcx);
			try {
				if (tk.getType() != CToken.TK_RBRA) {
					pcx.recoverableError("[]が閉じていません");
				}
			} catch (RecoverableErrorException e) {
			}
			tk = ct.getNextToken(pcx); // 配列じゃ無いときに合わせて次の字句まで読む
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; constItem starts");
		if (ident != null) {
		}
		o.println(";;; constItem completes");
	}
}
