package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;

import lang.*;
import lang.c.*;

public class VoidDecl extends CParseRule {
	// voidDecl ::= VOID IDENT LPAR RPAR { COMMA IDENT LPAR RPAR } SEMI

	public VoidDecl(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_VOID;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // voidを読み飛ばす

		if (!Ident.isFirst(tk)) {
			pcx.recoverableError("intの次はidentです");
		}
		try {
			registerName(pcx, tk);
		} catch (RecoverableErrorException e) {
		}

		tk = ct.getNextToken(pcx); // LPARを読む
		if (tk.getType() != CToken.TK_LPAR) {
			pcx.recoverableError("(が必要です");
		}
		tk = ct.getNextToken(pcx); // RPARを読む
		if (tk.getType() != CToken.TK_RPAR) {
			pcx.recoverableError("()が閉じていません");
		}

		tk = ct.getNextToken(pcx); // ,または;を読む
		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!Ident.isFirst(tk)) {
				pcx.recoverableError(",の次はidentです");
				ct.skipTo(pcx, CToken.TK_SEMI);
				return;
			}
			try {
				registerName(pcx, tk);
			} catch (RecoverableErrorException e) {
			}

			tk = ct.getNextToken(pcx); // LPARを読む
			if (tk.getType() != CToken.TK_LPAR) {
				pcx.recoverableError("(が必要です");
			}
			tk = ct.getNextToken(pcx); // RPARを読む
			if (tk.getType() != CToken.TK_RPAR) {
				pcx.recoverableError("()が閉じていません");
			}

			tk = ct.getNextToken(pcx); // ,を読む
		}

		if (tk.getType() != CToken.TK_SEMI) {
			ct.skipTo(pcx, CToken.TK_SEMI);
			pcx.recoverableError("VoidDecl: ;が必要です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; voidDecl starts");
		o.println(";;; voidDecl completes");
	}

	private void registerName(CParseContext pcx, CToken tk) throws RecoverableErrorException {
		String name = tk.getText();
		CSymbolTableEntry entry = new CSymbolTableEntry(CType.getCType(CType.T_err), 1, true);
		if ( !pcx.getSymbolTable().registerGlobal(name, entry) ) {
			pcx.recoverableError("すでに宣言されている変数です");
		}
	}
}
