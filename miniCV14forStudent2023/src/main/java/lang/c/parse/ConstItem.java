package lang.c.parse;

import static org.hamcrest.Matchers.endsWith;

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
	private boolean isGlobal;
	private CSymbolTableEntry entry;
	private CToken constIdentToken;

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
			constIdentToken = tk;
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
		this.setValue(num.getValue());
		tk = ct.getCurrentToken(pcx); // declItemに合わせて次の字句まで読む

		// 変数登録
		final boolean isConst = true;
		size = 1;
		if (isExistMult) {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), size, isConst, this.getValue());
		} else {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_int), size, isConst, this.getValue());
		}
		isGlobal = pcx.getSymbolTable().isGlobalMode();
		if (isGlobal) {
			if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
				pcx.recoverableError("DeclItem: " + identName + "はすでに宣言されている変数です");
			}
		} else {
			if ( !pcx.getSymbolTable().registerLocal(identName, entry) ) {
				pcx.recoverableError("DeclItem: " + identName + "はすでに宣言されている変数です");
			}
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
			if (isGlobal) {
				o.println(identName + ":\t.WORD " + ((Number)num).getValue() + "\t\t\t; ConstItem:");
			} else {
				o.println("\tMOV\t#" + entry.getAddress() + ", R0\t; ConstItem: フレームポインタと変数アドレスの変異を取得<" + constIdentToken.toExplainString() + ">");
				o.println("\tADD\tR4, R0\t; ConstItem: 変数アドレスを計算する<" + constIdentToken.toExplainString() + ">");
				o.println("\tMOV\t#"+ ((Number)num).getValue() +", (R0)\t; ConstItem: 定数を代入する<" + constIdentToken.toExplainString() + ">");
			}
		}
		o.println(";;; constItem completes");
	}
}
