package lang.c.parse;
import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Function extends CParseRule {
	// function        ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declblock
	CParseRule ident, declBlock;
	String functionName;
	boolean isExistMult = false;
	boolean isVoid = false;
	String returnLabel = "";


	public Function(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_FUNC;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // funcを読み飛ばす

		if (tk.getType() == CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
			if (tk.getType() == CToken.TK_MULT) {
				tk = ct.getNextToken(pcx);
				isExistMult = true;
			}
		} else if (tk.getType() == CToken.TK_VOID) {
			tk = ct.getNextToken(pcx);
			isVoid = true;
		} else {
			pcx.recoverableError(tk.toExplainString() + "funcの後ろはintかvoidです");
		}

		if (tk.getType() == CToken.TK_IDENT) {
			ident = new Ident(pcx);
			functionName = tk.getText();
			registerFunction(pcx, tk);
			returnLabel = "RET_" + functionName + pcx.getSeqId();
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "関数名が必要です");
		}


		if (tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "関数名の後ろは()です");
		}
		if (tk.getType() == CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "()が閉じていません");
		}

		if (DeclBlock.isFirst(tk)) {
			declBlock = new DeclBlock(pcx, returnLabel);
			declBlock.parse(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "()の後ろはDeclBlockです");
		}
	}

	private void registerFunction(CParseContext pcx, CToken tk) throws FatalErrorException {
		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = true;
		final boolean isFunction = true;
		int size = 1;
		if (isVoid) {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_void), size, isConst, isFunction);
		} else if (isExistMult) {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_pint), size, isConst, isFunction);
		} else {
			entry = new CSymbolTableEntry(CType.getCType(CType.T_int), size, isConst, isFunction);
		}

		if (
			!pcx.getSymbolTable().registerGlobal(functionName, entry)
			&& !pcx.getSymbolTable().searchGlobal(functionName).verificateFunction(entry)
		) {
			pcx.recoverableError("Function: " + tk.toDetailExplainString() + " すでに使用されている名前です");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		declBlock.semanticCheck(pcx);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Function starts");
		o.println(String.format("%s:", functionName));
		o.println("\tMOV\tR4, (R6)+\t; DeclItem: 旧フレームポインタをスタックに退避する");
		o.println("\tMOV\tR6, R4\t; DeclItem: 現在のスタックの値をフレームポインタにする");
		if (declBlock != null) {
			declBlock.codeGen(pcx);
		}
		o.println(String.format("%s:", returnLabel));
		o.println("\tMOV\tR4, R6\t; Function: 局所変数の領域を開放する");
		o.println("\tRET\t; Function: サブルーチンから復帰する");
		o.println(";;; Function completes");
	}
}
