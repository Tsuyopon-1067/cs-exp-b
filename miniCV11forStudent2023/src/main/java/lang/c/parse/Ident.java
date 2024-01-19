package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
	// ident ::= IDENT
	CToken ident;
	private String identName;
	CSymbolTableEntry entry;

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
			pcx.warning("変数" + identName + "は宣言されていません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			/*
			String identText = ident.getText();
			int setType = CType.T_int;
			boolean isConstant = false;
			if (identText.startsWith("i_")) {
				setType = CType.T_int;
			} else if (identText.startsWith("ip_")) {
				setType = CType.T_pint;
			} else if (identText.startsWith("ia_")) {
				setType = CType.T_int_array;
			} else if (identText.startsWith("ipa_")) {
				setType = CType.T_pint_array;
			} else if (identText.startsWith("c_")) {
				setType = CType.T_int;
				isConstant = true;
			} else {
				pcx.warning("変数はi_，ip_，ia_，ipa_，c_のどれかから始まる必要があります");
			}
			*/
			if (entry == null) {
				setCType(CType.getCType(CType.T_err));
				pcx.warning("変数" + ident.getText() + "は宣言されていません");
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
			if (entry.isGlobal()) {
				o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
			} else {
				o.println("\tMOV\t#" + entry.getAddress() + ", R0\t; Ident: フレームポインタと変数アドレスの変異を取得<" + ident.toExplainString() + ">");
				o.println("\tADD\tR4, R0\t; Ident: 変数アドレスを計算する<" + ident.toExplainString() + ">");
				o.println("\tMOV\tR0, (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
			}
		}
		o.println(";;; ident completes");
	}
}
