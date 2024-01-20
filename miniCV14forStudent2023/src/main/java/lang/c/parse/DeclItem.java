package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class DeclItem extends CParseRule {
	// declItem        ::= [ MULT ] IDENT [ LBRA NUMBER RBRA | LPAR RPAR ]
	CParseRule num;
	TypeList typeList;
	int size;
	String identName;
	boolean isFunction = false;
	boolean isGlobal;

	public DeclItem(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT || tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		this.setCType(CType.getCType(CType.T_int));

		if (tk.getType() == CToken.TK_MULT) {
			tk = ct.getNextToken(pcx); // *を読み飛ばす
			this.setCType(CType.getCType(CType.T_pint));
		}

		if (tk.getType() == CToken.TK_IDENT) {
			identName = tk.getText();
		} else {
			pcx.recoverableError(tk.toDetailExplainString() + "変数名が必要です");
		}

		tk = ct.getNextToken(pcx);

		if (tk.getType() == CToken.TK_LBRA) {
			if (this.getCType() == CType.getCType(CType.T_pint)) {
				this.setCType(CType.getCType(CType.T_pint_array));
			} else {
				this.setCType(CType.getCType(CType.T_int_array));
			}

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
			isFunction = true;
			tk = ct.getNextToken(pcx); // (を読み飛ばす
			if (TypeList.isFirst(tk)) {
				typeList = new TypeList(pcx, identName);
				typeList.parse(pcx);
				tk = ct.getCurrentToken(pcx);
			} else {
				tk = ct.getNextToken(pcx);
			}
			if (tk.getType() != CToken.TK_RPAR) {
				pcx.recoverableError(tk.toDetailExplainString() + "DeclItem: ()が閉じていません");
			}
			tk = ct.getNextToken(pcx); // 後ろに()[]が無いときに合わせて次の字句まで読む
		}

		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = false;
		if (this.getCType().getType() == CType.T_pint_array || this.getCType().getType() == CType.T_int_array) {
			size = ((Number)num).getValue();
		} else {
			size = 1;
		}
		entry = new CSymbolTableEntry(this.getCType(), size, isConst, isFunction);

		if (isFunction) {
			ArrayList<TypeItem> typeItemList = typeList.getTypeItemList();
			ArrayList<ParameterInfo> paramInfoList = new ArrayList<>();
			for (TypeItem typeItem : typeItemList) {
				paramInfoList.add(new ParameterInfo(typeItem.getCType(), identName));
			}
			FunctionInfo functionInfo = new FunctionInfo(identName, this.getCType(), identName, paramInfoList);
			functionInfo.setExistPrototype();
			entry.setFunctionInfo(functionInfo);
		}

		isGlobal = pcx.getSymbolTable().isGlobalMode();
		if (isFunction) {
			if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
				pcx.recoverableError("すでに宣言されている関数です");
			}
		} else if (isGlobal) {
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
