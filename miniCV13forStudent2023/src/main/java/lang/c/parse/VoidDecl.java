package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class VoidDecl extends CParseRule {
	// voidDecl        ::= VOID IDENT LPAR [ typelist ] RPAR { COMMA IDENT LPAR [ typeList ] RPAR } SEMI

	public VoidDecl(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_VOID;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // voidを読み飛ばす
		this.setCType(CType.getCType(CType.T_void));

		if (!Ident.isFirst(tk)) {
			pcx.recoverableError("VoidDecl: voidの次はidentです");
		}

		parseIdnetDeclaration(pcx, ct, tk);

		tk = ct.getCurrentToken(pcx); // ,または;を読む
		while (tk.getType() == CToken.TK_COMMA) {
			tk = ct.getNextToken(pcx); // ,を読み飛ばす
			if (!Ident.isFirst(tk)) {
				pcx.recoverableError(",の次はidentです");
				ct.skipTo(pcx, CToken.TK_SEMI);
				return;
			}
			parseIdnetDeclaration(pcx, ct, tk);
			tk = ct.getCurrentToken(pcx);
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

	private void registerName(CParseContext pcx, CToken tk, TypeList typeList) throws RecoverableErrorException {
		String identName = tk.getText();
		// 変数登録
		CSymbolTableEntry entry;
		final boolean isConst = false;
		final boolean isFunction = true;
		final int size = 1;
		entry = new CSymbolTableEntry(this.getCType(), size, isConst, isFunction);

		ArrayList<TypeItem> typeItemList = typeList.getTypeItemList();
		ArrayList<ParameterInfo> paramInfoList = new ArrayList<>();
		for (TypeItem typeItem : typeItemList) {
			paramInfoList.add(new ParameterInfo(typeItem.getCType(), identName));
		}
		this.setCType(CType.getCType(CType.T_void));
		FunctionInfo functionInfo = new FunctionInfo(identName, this.getCType(), identName, paramInfoList);
		functionInfo.setExistPrototype();
		entry.setFunctionInfo(functionInfo);

		if ( !pcx.getSymbolTable().registerGlobal(identName, entry) ) {
			pcx.recoverableError("すでに宣言されている関数です");
		}
	}

	private void parseIdnetDeclaration(CParseContext pcx, CTokenizer ct, CToken tk) throws FatalErrorException {
		CToken identToken = tk;
		boolean isExistTypeList = false;

		tk = ct.getNextToken(pcx); // LPARを読む
		if (tk.getType() != CToken.TK_LPAR) {
			pcx.recoverableError("(が必要です");
		}
		tk = ct.getNextToken(pcx); // LPARを読む
		if (TypeList.isFirst(tk)) {
			TypeList typeList = new TypeList(pcx, identToken.getText());
			typeList.parse(pcx);
			isExistTypeList = true;
			registerName(pcx, identToken, typeList);
			tk = ct.getCurrentToken(pcx); // RPARを読む
		} else {
			tk = ct.getNextToken(pcx); // RPARを読む
		}
		if (tk.getType() != CToken.TK_RPAR) {
			pcx.recoverableError("VoidDecl: ()が閉じていません");
		}

		tk = ct.getNextToken(pcx); // ,または;を読む
	}
}
