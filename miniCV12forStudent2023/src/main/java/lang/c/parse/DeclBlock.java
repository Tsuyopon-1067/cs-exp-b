package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import lang.c.parse.statement.Statement;

import lang.*;
import lang.c.*;

public class DeclBlock extends CParseRule {
    // declblock ::= LCUR { declaration } { statement } RCUR
	ArrayDeque<CParseRule> declareList = new ArrayDeque<CParseRule>();
	ArrayDeque<CParseRule> statmentList = new ArrayDeque<CParseRule>();
	int variableSize = 0;
	private String returnLabel;

	public DeclBlock(CParseContext pcx) {
		this.returnLabel = "";
	}

	public DeclBlock(CParseContext pcx, String returnLabel) {
		this.returnLabel = returnLabel;
	}

	public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		pcx.getSymbolTable().setupLocalSymbolTable();
        CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getNextToken(pcx); // {を読み飛ばす

		while (Declaration.isFirst(tk)) {
			declareList.add(new Declaration(pcx));
			declareList.getLast().parse(pcx);
			ct = pcx.getTokenizer();
			tk = ct.getNextToken(pcx);
		}

		while (Statement.isFirst(tk)) {
			statmentList.add(new Statement(pcx, returnLabel));
			statmentList.getLast().parse(pcx);
			ct = pcx.getTokenizer();
			tk = ct.getCurrentToken(pcx);
		}

		if (tk.getType() != CToken.TK_RCUR) {
			while (tk.getType() != CToken.TK_SEMI && !Statement.isFirst(tk) && tk.getType() != CToken.TK_EOF) {
				tk = ct.getNextToken(pcx);
			}
			if (tk.getType() == CToken.TK_SEMI) {
				tk = ct.getNextToken(pcx);
			}
			pcx.recoverableError(tk.toExplainString() + "}が閉じていません");
		}
		ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		variableSize = pcx.getSymbolTable().getAddressOffset();
		pcx.getSymbolTable().deleteLocalSymbolTable();
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (statmentList != null) {
			for (CParseRule statment : statmentList) {
				statment.semanticCheck(pcx);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; DeclBlock starts");
		o.println("\tMOV\tR4, (R6)+\t; DeclItem: フレームポインタをスタックに退避する");
		o.println("\tMOV\tR6, R4\t; DeclItem: 現在のスタックの値をフレームポインタにする");
		o.println("\tADD\t#" + variableSize + ", R6\t; DeclItem: 局所変数の領域を確保する");

		if (declareList != null) {
			for (CParseRule declaration : declareList) {
				declaration.codeGen(pcx);
			}
		}

		if (statmentList != null) {
			for (CParseRule statment : statmentList) {
				statment.codeGen(pcx);
			}
		}
		o.println("\tMOV\tR4, R6\t; DeclItem: 局所変数の領域を開放する");
		o.println("\tMOV\t-(R6), R4\t; DeclItem: 旧フレームポインタを復帰する");
		o.println(";;; DeclBlock completes");
	}
}