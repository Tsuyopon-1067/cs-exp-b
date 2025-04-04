package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayDeque;
import lang.c.parse.statement.Statement;

import lang.*;
import lang.c.*;

public class DeclBlock extends CParseRule {
    // declblock ::= LCUR { declaration } { statement } RCUR
	ArrayDeque<CParseRule> declareList = new ArrayDeque<CParseRule>();
	ArrayDeque<CParseRule> statmentList = new ArrayDeque<CParseRule>();
	int variableSize = 0;
	String returnLabel;

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

		try {
			if (tk.getType() != CToken.TK_RCUR) {
				pcx.recoverableError(tk.toExplainString() + "}が閉じていません");
			}
			ct.getNextToken(pcx); // ifは次の字句を読んでしまうのでそれに合わせる
		} catch (RecoverableErrorException e) {
		}
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

		if (declareList != null) {
			for (CParseRule declaration : declareList) {
				declaration.codeGen(pcx);
			}
		}

		o.println("\tADD\t#" + variableSize + ", R6\t; DeclItem: 局所変数の領域を確保する");

		if (statmentList != null) {
			for (CParseRule statment : statmentList) {
				statment.codeGen(pcx);
			}
		}
		o.println(";;; DeclBlock completes");
	}
}