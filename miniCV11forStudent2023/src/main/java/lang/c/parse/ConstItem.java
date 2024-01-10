package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConstItem extends CParseRule {
	// constItem   ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM
	CParseRule num;
	String identName;
	boolean isExistMult = false;
	boolean isExistAmp = false;
	int size;

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

		if (tk.getType() == CToken.TK_IDENT) {
			identName = tk.getText();
		} else {
			pcx.recoverableError("intの後ろは宣言する定数名です");
		}
		tk = ct.getNextToken(pcx);

		if (tk.getType() != CToken.TK_ASSIGN) {
			pcx.fatalError("=が必要です");
		}
		tk = ct.getNextToken(pcx); // =を読み飛ばす

		if (tk.getType() == CToken.TK_AMP) {
			tk = ct.getNextToken(pcx); // &を読み飛ばす
			isExistAmp = true;
		}

		if (tk.getType() == CToken.TK_NUM) {
			num = new Number(pcx);
		} else {
			pcx.recoverableError("右辺に数値が必要です");
		}
		num.parse(pcx);
		tk = ct.getCurrentToken(pcx); // declItemに合わせて次の字句まで読む

		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = true;
		final boolean isGlobal = true;
		size = 1;
		if (isExistMult) {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), size, isConst, isGlobal, 0);
		} else {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_int), size, isConst, isGlobal, 0);
		}
		if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
			pcx.recoverableError("ConstItem: すでに宣言されている変数です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (isExistMult != isExistAmp) {
			if (isExistMult) {
				pcx.warning("ConstItem: 右辺はpint型である必要があります");
			} else {
				pcx.warning("ConstItem: 右辺はint型である必要があります");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; constItem starts");
		if (num != null) {
			o.println(identName + ":\t.WORD " + ((Number)num).getValue() + "\t\t\t; ConstItem:");
		}
		o.println(";;; constItem completes");
	}
}
