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

		if (tk.getType() == CToken.TK_IDENT) {
			identName = tk.getText();
		} else {
			pcx.fatalError("左辺に変数が必要です");
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
				num.parse(pcx);
			} catch (RecoverableErrorException e) {
			}
			tk = ct.getCurrentToken(pcx); // numは次の字句まで呼んでしまう
			if (tk.getType() != CToken.TK_RBRA) {
				pcx.fatalError("[]が閉じていません");
			}
			tk = ct.getNextToken(pcx); // 配列じゃ無いときに合わせて次の字句まで読む
		}

		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = false;
		final boolean isGlobal = true;
		if (isArray) {
			size = ((Number)num).getValue();
			if (isExistMult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint_array), size, isConst, isGlobal, 0);
			} else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int_array), size, isConst, isGlobal, 0);
			}
		} else {
			size = 1;
			if (isExistMult) {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), size, isConst, isGlobal, 0);
			} else {
				entry = new CSymbolTableEntry(CType.getCType(CType.T_int), size, isConst, isGlobal, 0);
			}
		}
		try {
			if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
				pcx.recoverableError(identName + ": すでに宣言されている変数です");
			}
		} catch (RecoverableErrorException e) {
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; constItem starts");
		o.println(identName + ":\t.BLKW " + size + "\t\t\t; ConstItem:");
		o.println(";;; constItem completes");
	}
}
