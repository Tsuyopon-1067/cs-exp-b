package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {
	// declItem    ::= [ MULT ] IDENT [ LBRA NUM RBRA ]
	CParseRule num;
	int size;
	String identName;
	boolean isExistMult = false;
	boolean isArray = false;
	boolean isGlobal;

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
				identName = tk.getText();
			} else {
				pcx.recoverableError(tk.toDetailExplainString() + "変数名が必要です");
			}
		} catch (RecoverableErrorException e) {
		}

		tk = ct.getNextToken(pcx);

		if (tk.getType() == CToken.TK_LBRA) {
			isArray = true;
			tk = ct.getNextToken(pcx); // [を読み飛ばす

			if (tk.getType() == CToken.TK_NUM) {
				num = new Number(pcx);
			} else {
				pcx.recoverableError(tk.toDetailExplainString() + "[]内は数値です");
			}
			num.parse(pcx);
			tk = ct.getCurrentToken(pcx); // numは次の字句まで呼んでしまう
			if (tk.getType() != CToken.TK_RBRA) {
				pcx.recoverableError(tk.toDetailExplainString() + "[]が閉じていません");
			}
			tk = ct.getNextToken(pcx); // 後ろに()[]が無いときに合わせて次の字句まで読む
		} else if (tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx); // (を読み飛ばす
			if (tk.getType() != CToken.TK_RPAR) {
				pcx.recoverableError(tk.toDetailExplainString() + "()が閉じていません");
			}
			tk = ct.getNextToken(pcx); // 後ろに()[]が無いときに合わせて次の字句まで読む
		}

		if (tk.getType() != CToken.TK_SEMI) {
			pcx.recoverableError(tk.toDetailExplainString() + ";が必要です");
		}

		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = false;
		if (isArray) {
			size = ((Number)num).getValue();
			if (isExistMult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint_array), size, isConst);
			} else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int_array), size, isConst);
			}
		} else {
			size = 1;
			if (isExistMult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), size, isConst);
			} else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int), size, isConst);
			}
		}

		isGlobal = pcx.getSymbolTable().isGlobalMode();
		if (isGlobal) {
			if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
				pcx.recoverableError("すでに宣言されている変数です");
			}
		} else {
			if ( !pcx.getSymbolTable().registerLocal(identName, entry) ) {
				pcx.recoverableError("すでに宣言されている変数です");
			}
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; declItem starts");
		if (isGlobal) {
			o.println(identName + ":\t.BLKW " + size + "\t\t\t; DeclItem:");
		}
		o.println(";;; declItem completes");

	}
}
