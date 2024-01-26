package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
	// ident ::= IDENT
	CToken ident;
	private String identName;
	CSymbolTableEntry entry;
	boolean isFunction = false;

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
			pcx.warning("Ident: 変数" + identName + "は宣言されていません");
		}

		// 関数の場合は隠れた局所変数を作って登録する
		if (!pcx.getSymbolTable().isGlobalMode() && entry != null) {
			if (entry.isFunction()) {
				String identNameForEntry = identName + pcx.getSeqId();
				CSymbolTableEntry functionEntry = pcx.getSymbolTable().searchGlobal(identName); // グローバルで登録されている本物の定義
				FunctionInfo functionInfo = functionEntry.getFunctionInfo();
				CType returnType = functionInfo.getReturnType();

				final boolean IS_CONST = true;
				final boolean IS_FUNCTION = true;
				entry = new CSymbolTableEntry(returnType, 1, IS_CONST, IS_FUNCTION);
				entry.setFunctionInfo(functionInfo);

				pcx.getSymbolTable().registerLocal(identNameForEntry, entry);
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (ident != null) {
			if (entry == null) {
				setCType(CType.getCType(CType.T_err));
				return;
			}
			if (entry.isConstant()) {
				this.setConstant(true);
				this.setValue(entry.getValue());
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
			if (!entry.isFunction()) {
				if (entry.isGlobal()) {
					o.println("\tMOV\t#" + ident.getText() + ", R0\t; Ident: 変数アドレスをR0に用意する<" + ident.toExplainString() + ">");
					o.println("\tMOV\tR0, (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
				} else {
					o.println("\tMOV\t#" + entry.getAddress() + ", R0\t; Ident: フレームポインタと変数アドレスの変位を取得<" + ident.toExplainString() + ">");
					o.println("\tADD\tR4, R0\t; Ident: 変数アドレスを計算する<" + ident.toExplainString() + ">");
					o.println("\tMOV\tR0, (R6)+\t; Ident: 変数アドレスを積む<" + ident.toExplainString() + ">");
				}
			} else {
				o.println("\tJSR\t#" + ident.getText() + "\t; Ident: 関数へジャンプ<" + ident.toExplainString() + ">");
			}
		}
		o.println(";;; ident completes");
	}

	public void setEntry(CSymbolTableEntry entry) {
		this.entry = entry;
	}
	public CSymbolTableEntry getEntry() {
		return entry;
	}
	public boolean getIsFunction() {
		return isFunction;
	}
	public String getIdentName() {
		return identName;
	}
}
