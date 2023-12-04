package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConstItem extends CParseRule {
	// constItem   ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM
	CParseRule ident, num;
	boolean isExistMult = false;
	boolean isExistAmp = false;

	public ConstItem(CParseContext pcx) {
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

		try {
			if (tk.getType() != CToken.TK_ASSIGN) {
				pcx.recoverableError("=が必要です");
			}
		} catch (RecoverableErrorException e) {
		}
		tk = ct.getNextToken(pcx); // =を読み飛ばす

		if (tk.getType() == CToken.TK_AMP) {
			tk = ct.getNextToken(pcx); // &を読み飛ばす
			isExistAmp = true;
		}

		try {
			if (tk.getType() == CToken.TK_NUM) {
				num = new Number(pcx);
			} else {
				pcx.recoverableError("右辺に数値が必要です");
			}
		} catch (RecoverableErrorException e) {
		}
		tk = ct.getNextToken(pcx); // declItemに合わせて次の字句まで読む
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
