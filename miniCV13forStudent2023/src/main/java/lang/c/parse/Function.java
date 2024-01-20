package lang.c.parse;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class Function extends CParseRule {
	// function        ::= FUNC ( INT [ MULT ] | VOID ) IDENT LPAR RPAR declblock
	CParseRule ident, argList, declBlock;
	String functionName;
	boolean isExistMult = false;
	boolean isVoid = false;
	String returnLabel = "";
	int returnValueType;
	final int TYPE_INT = 0;
	final int TYPE_PINT = 1;
	final int TYPE_VOID = 2;
	private FunctionInfo functionInfo;
	private CToken identToken;

	public Function(CParseContext pcx) {
		this.setCType(CType.getCType(CType.T_err));
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_FUNC;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		pcx.getSymbolTable().setupLocalSymbolTable();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // funcを読み飛ばす

		if (tk.getType() == CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
			returnValueType = TYPE_INT;
			this.setCType(CType.getCType(CType.T_int));
			if (tk.getType() == CToken.TK_MULT) {
				tk = ct.getNextToken(pcx);
				isExistMult = true;
				returnValueType = TYPE_PINT;
				this.setCType(CType.getCType(CType.T_pint));
			}
		} else if (tk.getType() == CToken.TK_VOID) {
			tk = ct.getNextToken(pcx);
			isVoid = true;
			returnValueType = TYPE_VOID;
			this.setCType(CType.getCType(CType.T_void));
		} else {
			pcx.recoverableError(tk.toExplainString() + "funcの後ろはintかvoidです");
		}

		if (tk.getType() == CToken.TK_IDENT) {
			ident = new Ident(pcx);
			functionName = tk.getText();
			identToken = tk;
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
		if (ArgList.isFirst(tk)) {
			argList = new ArgList(pcx);
			argList.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if (tk.getType() == CToken.TK_RPAR) {
			tk = ct.getNextToken(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "Function: ()が閉じていません");
		}

		registerFunction(pcx, identToken);

		if (DeclBlock.isFirst(tk)) {
			declBlock = new DeclBlock(pcx, functionInfo);
			declBlock.parse(pcx);
		} else {
			pcx.recoverableError(tk.toExplainString() + "()の後ろはDeclBlockです");
		}

		pcx.getSymbolTable().deleteLocalSymbolTable();
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
		returnLabel = "RET_" + functionName + pcx.getSeqId();

		ArrayDeque<CParseRule> argItems = new ArrayDeque<>();
		if (argList != null) {
			argItems = ((ArgList)argList).getArgItems();
		}
		ArrayList<ParameterInfo> paramInfoList = new ArrayList<>();
		for (CParseRule argItem : argItems) {
			paramInfoList.add(new ParameterInfo(argItem.getCType(), ((ArgItem)argItem).getName())); // argItemは例外的にparseでCTypeをセットしている
		}

		functionInfo = new FunctionInfo(functionName, this.getCType(), returnLabel, paramInfoList);
		entry.setFunctionInfo(functionInfo);

		CSymbolTableEntry prototypeEntry = pcx.getSymbolTable().searchGlobal(functionName);
		if (prototypeEntry == null) {
			pcx.getSymbolTable().registerGlobal(functionName, entry);
			return;
		}

		// プロトタイプ宣言がある場合
		if (!prototypeEntry.getFunctionInfo().getIsExistPrototype()) {
			pcx.warning("Function: " + tk.toDetailExplainString() + " この関数名はすでに使用されています");
		} else if (!entry.verificateFunction(prototypeEntry)) {
			pcx.warning("Function: " + tk.toDetailExplainString() + " プロトタイプ宣言と定義が異なります");
		} else {
			prototypeEntry.setFunctionInfo(functionInfo);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		declBlock.semanticCheck(pcx);
		if (functionInfo.getReturnType() != CType.getCType(CType.T_void) && !functionInfo.getIsExistReturn()) {
			pcx.warning("関数" + functionName + "はreturnする必要があります");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		int variableSize = -1;
		if (declBlock != null) {
			variableSize = ((DeclBlock) declBlock).getVariableSize();
		}
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Function starts");
		o.println(String.format("%s:", functionName));
		o.println("\tMOV\tR4, (R6)+\t; DeclItem: 旧フレームポインタをスタックに退避する");
		o.println("\tMOV\tR6, R4\t; DeclItem: 現在のスタックの値をフレームポインタにする");
		o.println("\tADD\t#" + variableSize + ", R6\t; DeclItem: 局所変数の領域を確保する");
		if (declBlock != null) {
			declBlock.codeGen(pcx);
		}
		o.println(String.format("%s:", returnLabel));
		o.println("\tMOV\tR4, R6\t; Function: 局所変数の領域を開放する");
		o.println("\tMOV\t-(R6), R4\t; DeclItem: 旧フレームポインタを復帰する");
		o.println("\tRET\t; Function: サブルーチンから復帰する");
		o.println(";;; Function completes");
	}
}
