package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
	// ident ::= IDENT
	CToken ident;
	private String identName, functionLabel;
	CSymbolTableEntry entry;
	boolean isFunction = false;
	private int seqId;

	public Ident(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		identName = tk.getText();
		ident = tk;

		if (pcx.getSymbolTable().searchLocal(identName) != null) {
			entry = pcx.getSymbolTable().searchLocal(identName);
		} else if (pcx.getSymbolTable().searchGlobal(identName) != null) {
			entry = pcx.getSymbolTable().searchGlobal(identName);
		} else {
			pcx.warning(identName + "は宣言されていない名前です");
		}

		if (entry != null) {
			isFunction = entry.isFunction();
		}
		if (isFunction) {
			seqId = pcx.getSeqId();
			functionLabel = identName+seqId;
			pcx.getSymbolTable().registerLocal(functionLabel, entry); // 関数を局所変数として登録
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			if (entry == null) {
				setCType(CType.getCType(CType.T_err));
				return;
			}

			int setType = entry.GetCType().getType();
			boolean isConstant = entry.isConstant();
			this.setCType(CType.getCType(setType));
			this.setConstant(isConstant);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ident starts");
		if (ident != null && entry != null) {
			if (isFunction) {
				o.println("\tJSR\t#" + identName + "\t; Ident: サブルーチンにジャンプする<" + ident.toExplainString() + ">");
				o.println("\tMOV\t#" + entry.getAddress() + ", R1\t; Ident: フレームポインタと変数アドレスの変異を取得<" + ident.toExplainString() + ">");
				o.println("\tADD\tR0, R0\t; Ident: 変数アドレスを計算する<" + ident.toExplainString() + ">");
				o.println("\tMOV\tR1, (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
				o.println("\tMOV\tR0, (R1)\t; Ident: 変数アドレスに戻り値を代入する<" + ident.toExplainString() + ">");
			} else if (entry.isGlobal()) {
				o.println("\tMOV\t#" + identName + ", (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
			} else {
				o.println("\tMOV\t#" + entry.getAddress() + ", R0\t; Ident: フレームポインタと変数アドレスの変異を取得<" + ident.toExplainString() + ">");
				o.println("\tADD\tR4, R0\t; Ident: 変数アドレスを計算する<" + ident.toExplainString() + ">");
				o.println("\tMOV\tR0, (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
			}
		}
		o.println(";;; ident completes");
	}
}
